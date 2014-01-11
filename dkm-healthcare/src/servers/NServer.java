package servers;

import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import protocols.P3protocol;
import protocols.P3protocol.Site;
import servers.ServerSocketThread.ServerType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import others.Config;


/**
 * Class for Notification Server
 * @author dudu
 *
 */

public class NServer {
	
	Config config;
	String historyQuery = "select * from historia_powiadomienia";
	String left5Query = "select * from recepty_powiadomienia_5";
	String expireQuery = "select * from recepty_powiadomienia_dzis";
	
	public NServer() {
		config = new Config();
	}
	
	public static enum NotifyType {
		HISTORY, LEFT5, EXPIRE
	}
	
	/**
	 * Class to ping DB Servers; extends TimerTask to run it periodically with Timer
	 * Creates an instance of P3protocol and runs function PingServers.
	 * Then, if in returned ArrayList false value occurs, runs function to notify administrator
	 * @author dudu
	 *
	 */
	
	public class Pinger extends TimerTask {
		
		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify, config);
			ArrayList<Boolean> list = protocol.pingServers();
			for (int i=0; i<protocol.dbAdresses.size(); i++) {
				if (list.get(i) == Boolean.FALSE) {
					NotifyAdmin(protocol.dbAdresses.get(i)); 
				}
			}
		}
	}
	
	/**
	 * Class to send notifications about changes in history.
	 * Similar to Pinger
	 * @author dudu
	 *
	 */
	
	public class Notifier extends TimerTask {
		
		String query;
		NotifyType type;
		
		public Notifier(String query, NotifyType type) {
			this.query = query;
			this.type = type;
		}

		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify, config);
			ResultSet result = protocol.getInfo(query);
			NotifyPatients (result, type);
		}
		
	}
	
	
	
	/**
	 * class to send mail. Uses methods from JavaMail library.
	 *  Mail is sent from "adminMail" to "email".
	 * Public, because NGServer uses it
	 * @param email
	 * @param mailtext
	 */
	
	public void SendMail(String email, String mailtext) {
		String host = "smtp.gmail.com";
	    String username = "dkm.dkmhealthcare";
	    String password = config.password;
	    Properties props = new Properties();
	    // set any needed mail.smtps.* properties here
	    Session session = Session.getInstance(props);
	    MimeMessage msg = new MimeMessage(session);
	    Transport t = null;
	    try {
		    msg.setFrom(new InternetAddress(email));
	        msg.addRecipient(Message.RecipientType.TO,
	                         new InternetAddress("dokaptur@gmail.com"));
	        msg.setSubject("Server error");
	        msg.setText(mailtext);

	        t = session.getTransport("smtps");
		
			t.connect(host, username, password);
			t.sendMessage(msg, msg.getAllRecipients());
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				t.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
	    }
	}
	
	/**
	 * Class to notify patients. It takes info from ResultSet, prepares message (which depends on Request) and then runs SendMail
	 * public static, because NGServer uses it as well.
	 * @param rs
	 * @param req
	 */
	
	public void NotifyPatients (ResultSet rs, NotifyType req) {
		if (rs == null) return;
		try {
			while (rs.next()) {
				String mail = rs.getString("email");
				String imie = rs.getString("imie");
				String nazwisko = rs.getString("nazwisko");
				String recepta = "";
				if (! req.equals(NotifyType.HISTORY)) {
					recepta = rs.getString("recepta");
				}
				String mailtext = "Dear " + imie + " " + nazwisko + "! \n";
				if (req.equals(NotifyType.HISTORY)) {
					mailtext += "Your history has been changed today. Check it!";
				} else if (req.equals(NotifyType.LEFT5)) {
					mailtext += "Your prescription (" + recepta + ") is going to expire within 5 days. \n Buy it as soon as possible!";
				} else {
					mailtext += "Your prescription (" + recepta + ") expires today.";
				}
				SendMail(mail, mailtext);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Class to notify administrator about problems with server 
	 * Public static, because NGServer uses it as well.
	 * @param addr (address of broken server) 
	 */
	
	public void NotifyAdmin(InetSocketAddress addr) {
		String email = "dkm.dkmhealthcare@gmail.com";
		String mailtext = "An error occured on Server " + addr.getHostString() + " ! \n Check it as soon as possible!";
		SendMail(email, mailtext);
	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		NServer nserver = new NServer();
		
		
		
		(new Timer()).scheduleAtFixedRate(nserver.new Pinger(), nserver.config.midnight.getTime(), 3600 * 1000);
		
		(new Timer()).scheduleAtFixedRate(nserver.new Notifier(nserver.historyQuery, NotifyType.HISTORY), nserver.config.pm4.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(nserver.new Notifier(nserver.left5Query, NotifyType.LEFT5), nserver.config.midnight.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(nserver.new Notifier(nserver.expireQuery, NotifyType.EXPIRE), nserver.config.midnight.getTime(), 3600 * 1000 * 12);
		
		try {
			ServerSocket server = new ServerSocket(2006);
			while (true) {
				Socket socket = server.accept();
				ServerSocketThread thread = new ServerSocketThread(socket, ServerType.N, nserver.config);
				thread.run();
			}
		} catch (Exception e) {
		} 
	}

}
