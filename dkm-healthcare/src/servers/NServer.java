package servers;

import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import protocols.P3protocol;
import protocols.P3protocol.Request;
import protocols.P3protocol.Site;
import servers.ServerSocketThread.ServerType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Class for Notification Server
 * @author dudu
 *
 */

public class NServer {
	
	/**
	 * Fields to get time for Timer. They are static, because NGServer uses those as well. 
	 */
	
	public static Calendar midnight;
	public static Calendar pm4;
	
	
	static {
		midnight.set(Calendar.HOUR, 1);
		midnight.set(Calendar.AM_PM, Calendar.AM);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		
		pm4.set(Calendar.HOUR, 4);
		pm4.set(Calendar.AM_PM, Calendar.PM);
		pm4.set(Calendar.MINUTE, 0);
		pm4.set(Calendar.SECOND, 0);
	}
	
	/**
	 * administrator's email address
	 */
	
	public static String adminMail = "dokaptur@gmail.com";
	
	/**
	 * Class to ping DB Servers; extends TimerTask to run it periodically with Timer
	 * Creates an instance of P3protocol and runs function PingServers.
	 * Then, if in returned ArrayList false value occurs, runs function to notify administrator
	 * @author dudu
	 *
	 */
	
	public static class Pinger extends TimerTask {
		
		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify);
			ArrayList<Boolean> list = protocol.pingServers();
			for (int i=0; i<protocol.dbAdresses.size(); i++) {
				if (list.get(i) == Boolean.FALSE) {
					NServer.NotifyAdmin(protocol.dbAdresses.get(i)); 
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
	
	public static class History_Notifier extends TimerTask {

		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify);
			ResultSet result = protocol.getInfo(Request.HISTORY_CHANGE);
			NotifyPatients (result, Request.HISTORY_CHANGE);
		}
		
	}
	
	/**
	 * Class to send notifications about prescriptions expiring within 5 days.
	 * Similar to Pinger
	 * @author dudu
	 *
	 */
	
	public static class Left5_Notifier extends TimerTask {

		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify);
			ResultSet result = protocol.getInfo(Request.PRECSR_LEFT5);
			NotifyPatients (result, Request.PRECSR_LEFT5);
		}
		
	}
	
	/**
	 * Class to send notifications about  prescriptions expiring today.
	 * Similar to Pinger
	 * @author dudu
	 *
	 */
	
	public static class Expire_Notifier extends TimerTask {

		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify);
			ResultSet result = protocol.getInfo(Request.PRESC_EXPIRE);
			NotifyPatients (result, Request.PRESC_EXPIRE);
		}
		
	}
	
	/**
	 * Static class to send mail. Uses methods from JavaMail library.
	 *  Mail is sent from "adminMail" to "email".
	 * It is static, because other static functions call it.
	 * Public, because NGServer uses it
	 * @param email
	 * @param mailtext
	 */
	
	public static void SendMail(String email, String mailtext) {
		String host = "smtp.gmail.com";
	    String username = "dkm.dkmhealthcare";
	    String password = " ";// here comes the password. I don't want to public in on GitHub ;)
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
	
	public static void NotifyPatients (ResultSet rs, Request req) {
		if (rs == null) return;
		try {
			while (rs.next()) {
				String mail = rs.getString("email");
				String imie = rs.getString("imie");
				String nazwisko = rs.getString("nazwisko");
				String recepta = "";
				if (! req.equals(Request.HISTORY_CHANGE)) {
					recepta = rs.getString("recepta");
				}
				String mailtext = "Dear " + imie + " " + nazwisko + "! \n";
				if (req.equals(Request.HISTORY_CHANGE)) {
					mailtext += "Your history has been changed today. Check it!";
				} else if (req.equals(Request.PRECSR_LEFT5)) {
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
	
	public static void NotifyAdmin(InetSocketAddress addr) {
		String email = "dkm.dkmhealthcare@gmail.com";
		String mailtext = "An error occured on Server " + addr.getHostString() + " ! \n Check it as soon as possible!";
		SendMail(email, mailtext);
	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		
		(new Timer()).scheduleAtFixedRate(new Pinger(), midnight.getTime(), 3600 * 1000);
		
		(new Timer()).scheduleAtFixedRate(new History_Notifier(), pm4.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(new Left5_Notifier(), midnight.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(new Expire_Notifier(), midnight.getTime(), 3600 * 1000 * 12);
		
		try {
			ServerSocket server = new ServerSocket(2006);
			while (true) {
				Socket socket = server.accept();
				ServerSocketThread thread = new ServerSocketThread(socket, ServerType.N);
				thread.run();
			}
		} catch (Exception e) {
		} 
	}

}
