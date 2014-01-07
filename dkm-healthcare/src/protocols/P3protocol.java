package protocols;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;


/**
 * An application-level protocol for communication between notifier server and data basis servers.
 * @author dudu
 */

public class P3protocol {
	
	/**
	 * addresses of DataBasis servers
	 */
	
	public ArrayList<InetSocketAddress> dbAdresses = new ArrayList<>();
	
	/**
	 * addresses of Notification servers
	 */
	public ArrayList<InetSocketAddress> nAdresses = new ArrayList<>();
	
	/**
	 * timeout for connection
	 */
	int timeout = 1000;
	
	/**
	 * enum to determine if we are DBServer or N/NG Server
	 */
	public static enum Site {
		Notify, DB;
	}
	
	/**
	 * enum to determine what do we want from DBServer
	 */
	public static enum Request {
		PING, HISTORY_CHANGE, PRECSR_LEFT5, PRESC_EXPIRE;
	}
	
	
	Site site;
	
	/**
	 * constructor;
	 * we fill in it addresses of servers - that can be "hardcoded"
	 * @param site - we tell protocol as which server we will use it
	 */
	
	
	public  P3protocol(Site site) {
		this.site = site;
		
		//tmp!!
		dbAdresses.add(new InetSocketAddress("localohost", 2004));
		dbAdresses.add(new InetSocketAddress("localohost", 2005));
		
		nAdresses.add(new InetSocketAddress("localohost", 2006));
		nAdresses.add(new InetSocketAddress("localohost", 2007));
	}
	
	/**
	 * The most important method. Here is implemented whole communication.
	 * @param socket, request, connection with Data Basis on DB Server
	 * @return null if we are in DBServer or if error occurs. Otherwise Boolean while pinging and ResultSet while asking DB about some info.  
	 *
	 * @throws Exception
	 */
	
	public Object talk(Socket socket, Request request, Connection con) throws Exception {
		PrintWriter wr = new PrintWriter (socket.getOutputStream());
		Scanner sc = new Scanner (socket.getInputStream());
		Object obj = null;
		
		if (site == Site.DB) {
			boolean flag = false;
			for (InetSocketAddress addr : nAdresses) {
				if (socket.getInetAddress().equals(addr.getAddress())) {
					flag = true;
				}
			}
			if (!flag) {
				wr.write("I don't know you and Mom always told me I mustn't talk to the stranger! Bye!");
				sc.close(); wr.close();
				return null;
			}
			else wr.write("P3 Protocol. What do you want?");
			
			String ans = sc.nextLine();
			if (ans.equals("Ping!")) {
				wr.write("Pong!");
				sc.close(); wr.close();
				return null;
			}
			else if (ans.equals("Give me info!")) {
				for (int i=0; i<10; i++) {
					wr.write("Ok, give me querry");
					String sql = sc.nextLine();
					ResultSet result = null;
					Statement stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					try {
						result = stat.executeQuery(sql);
					} catch (Exception e) {
						result = null;
					}
					wr.close();
					ObjectOutputStream obstr = new ObjectOutputStream(socket.getOutputStream());
					obstr.writeObject(result);
					obstr.close();
					wr = new PrintWriter(socket.getOutputStream());
					String ans2 = sc.nextLine();
					if (ans2.equals("Ok, got it")) {
						wr.write("Ok, bye!");
						sc.close(); wr.close();
						return null;
					} 
					else if (ans2.equals("Something went wrong!")) {
						continue;
					}
					else {
						wr.write("Protocol error! Bye!");
						sc.close(); wr.close();
						return null;
					}
				}
				wr.write("I can't. Tell it to admin. Bye!");
				sc.close(); wr.close();
				return null;
			}
			else {
				sc.close(); wr.close();
				throw (new ProtocolException());
			}
			
		} else { // site = Notify
			
			wr.write("P3");
			if (!sc.nextLine().equals("P3 Protocol. What do you want?")) {
				sc.close(); wr.close();
				return null;
			}
			if (request == Request.PING) {
				wr.write("Ping!");
				if (!sc.nextLine().equals("Pong!")) {
					sc.close(); wr.close();
					return null;
				}
				sc.close(); wr.close();
				return new Boolean(true);
			} else {
				wr.write("Give me info!");
				String s = sc.nextLine();
				while (s.equals("Ok, give me querry")) {
					String sql = "";
					if (request == Request.HISTORY_CHANGE) {
						sql = "select * from \"dkm-healthcare\".historia_powiadomienia";
					} else if (request == Request.PRECSR_LEFT5) {
						sql = "select * from \"dkm-healthcare\".recepty_powiadomienia_5";
					} else {
						sql = "select * from \"dkm-healthcare\".recepty_powiadomienia_dzis";
					}
					wr.write(sql);
					sc.close();
					ObjectInputStream obstr = new ObjectInputStream(socket.getInputStream());
					obj = obstr.readObject();
					obstr.close();
					sc = new Scanner (socket.getInputStream());
					if (obj != null && obj instanceof ResultSet) {
						wr.write("Ok, got it");
					} else {
						wr.write("Something went wrong!");
					}
					s = sc.nextLine();
				}
				wr.close(); sc.close();
				if (s.equals("Ok, bye!")) {
					return (ResultSet) obj;
				} else return null;
				
			}
		}
	}
	
	/**
	 * checks if DBservers are available
	 * @return Array of "availability"
	 */
	
	
	public ArrayList<Boolean> pingServers() {
		ArrayList<Boolean> list = new ArrayList<>();
		for (InetSocketAddress addr : dbAdresses) {
			Socket socket = new Socket();
			boolean flag = false;
			for (int i=0; i<100; i++) {
				try {
					socket.connect(addr, timeout);
					Boolean b = (Boolean) talk(socket, Request.PING, null);
					if (b.equals(Boolean.TRUE)) {
						list.add(b);
						flag = true;
						break;
					} else continue;
					
				} catch (Exception e) {
					try {
						Thread.sleep(3000);
					} catch (Exception e1) {
					}
				}
			}
			if (!flag) {
				list.add(Boolean.FALSE);
			}
		}
		return list;
	}
	
	/**
	 * asks DataBasis servers about prescriptions or modifications in history we should send notifications about
	 *  (together with email addresses and names of patients) or last modified history 
	 * @param enum to determine what do we want to ask about
	 * @return ResultSet from DBServers 
	 */
	
	public ResultSet getInfo(Request request) {
		for (InetSocketAddress addr : dbAdresses) {
			Socket socket = new Socket();
			for (int i=0; i<100; i++) {
				try {
					socket.connect(addr, timeout);
					ResultSet b = (ResultSet) talk(socket, request, null);
					if (b != null) {
						return b;
					}
					
				} catch (Exception e) {
					try {
						Thread.sleep(3000);
					} catch (Exception e1) {
					}
				}
			}
		}
		return null;
	}
	
	

}
