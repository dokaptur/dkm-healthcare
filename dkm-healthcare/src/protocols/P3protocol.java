package protocols;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

//import javax.net.ssl.SSLServerSocket;
//import javax.net.ssl.SSLServerSocketFactory;
//import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.rowset.CachedRowSetImpl;

import others.Config;
import servers.BDServer;


/**
 * An application-level protocol for communication between notifier server and data basis servers.
 * @author dudu
 */

public class P3protocol {
	
	/**
	 * Object to create SSLSockets
	 */
	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	
	/**
	 * object with configuration
	 */
	Config config;
	
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
		PING, INFO;
	}
	
	/**
	 * instance of enum Site - filled in inside constructor function
	 */
	Site site;
	
	/**
	 * constructor;
	 * we fill in here addresses of servers from config object
	 * @param site - we tell protocol as which server we will use it
	 * @param config - we give to protocol an instance of Config class
	 */
	public  P3protocol(Site site, Config config) {
		this.site = site;
		this.config = config;
		
		dbAdresses.add(config.BD1addr);
		dbAdresses.add(config.BD2addr);
		
		nAdresses.add(config.Naddr);
		nAdresses.add(config.NGaddr);
		
	}
	
	/**
	 * The most important method. Here is implemented whole communication.
	 * @param socket, request, SQL query, connection with Data Basis on DB Server
	 * @return null if we are in DBServer or if error occurs. Otherwise Boolean while pinging and ResultSet while asking DB about some info.  
	 *
	 * @throws Exception
	 */
	
	public Object talk(Socket socket, Request request, String query, BDServer bd) throws Exception {
		
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		PrintWriter wr = new PrintWriter (os);
		BufferedReader sc = new BufferedReader(new InputStreamReader(is));
		Object obj = null;
		
		if (site == Site.DB) {
			boolean flag = false;
			for (InetSocketAddress addr : nAdresses) {
				if (socket.getInetAddress().equals(addr.getAddress())) {
					flag = true;
				}
			}
			if (!flag) {
				wr.println("I don't know you and Mom always told me I mustn't talk to the stranger! Bye!"); wr.flush();
				sc.close(); wr.close();
				return null;
			}
			else {
				wr.println("P3 Protocol. What do you want?"); wr.flush();
			}
			
			String ans = sc.readLine(); System.out.println(ans);
			if (ans.equals("Ping!")) {
				wr.println("Pong!"); wr.flush();
				sc.close(); wr.close();
				return null;
			}
			else if (ans.equals("Give me info!")) {
				for (int i=0; i<10; i++) { // tries to send result max 10 times
					wr.println("Ok, give me query"); wr.flush();
					
					// get CachedRowSetImpl object from DB
					String sql = sc.readLine(); System.out.println(sql);
					
					CachedRowSetImpl crs = bd.executeQuery(sql);
					
					wr.println("Port number for new connection?"); wr.flush();
					String s = sc.readLine(); System.out.println(s);
					int port = Integer.parseInt(s);
					//SSLSocket objsocket = (SSLSocket) factory.createSocket();
					Socket objsocket = new Socket();
					objsocket.connect(new InetSocketAddress(socket.getInetAddress(), port), 100);
					ObjectOutputStream oos = new ObjectOutputStream(objsocket.getOutputStream());
					oos.writeObject(crs); oos.flush();
					oos.close(); objsocket.close();
					
					crs.close();
					
					String ans2 = sc.readLine(); System.out.println(ans2);
					if (ans2.equals("Ok, got it")) {
						wr.println("Ok, bye!"); wr.flush();
						sc.close(); wr.close();
						return null;
					} 
					else if (ans2.equals("Something went wrong!")) {
						continue;
					}
					else {
						wr.println("Protocol error! Bye!"); wr.flush();
						sc.close(); wr.close();
						return null;
					}
				}
				wr.println("I can't. Tell it to admin. Bye!"); wr.flush();
				sc.close(); wr.close();
				return null;
			}
			else {
				sc.close(); wr.close();
				throw (new ProtocolException());
			}
			
		} else { // site = Notify
			
			wr.println("P3"); wr.flush();
			String s = sc.readLine(); System.out.println(s);
			if (!s.equals("P3 Protocol. What do you want?")) {
				sc.close(); wr.close();
				return null;
			}
			if (request == Request.PING) {
				wr.println("Ping!"); wr.flush();
				s = sc.readLine(); System.out.println(s);
				if (!s.equals("Pong!")) {
					sc.close(); wr.close();
					return null;
				}
				sc.close(); wr.close();
				return new Boolean(true);
			} else {
				wr.println("Give me info!"); wr.flush();
				s = sc.readLine(); System.out.println(s);
				while (s.equals("Ok, give me query")) {
					wr.println(query); wr.flush();
					
					//SSLServerSocketFactory sfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
					//SSLServerSocket objss = (SSLServerSocket) sfactory.createServerSocket(0);
					ServerSocket objss = new ServerSocket(0);
					s = sc.readLine(); System.out.println(); // Port number for new connection?
					wr.println(objss.getLocalPort()); wr.flush();
					Socket objsocket = objss.accept();
					ObjectInputStream obstr = new ObjectInputStream(objsocket.getInputStream());
					obj = obstr.readObject();
					obstr.close(); objsocket.close(); objss.close();
					
					if (obj != null && obj instanceof CachedRowSetImpl) {
						wr.println("Ok, got it"); wr.flush();
					} else {
						wr.println("Something went wrong!"); wr.flush();
					}
					s = sc.readLine(); System.out.println(s);
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
	 * @return Boolean Array of "availability"
	 */
	
	
	public ArrayList<Boolean> pingServers() {
		ArrayList<Boolean> list = new ArrayList<>();
		for (InetSocketAddress addr : dbAdresses) {
			Socket socket = new Socket();
			/*SSLSocket socket = null;
			try {
				socket = (SSLSocket) factory.createSocket();
			} catch (IOException e2) {
				e2.printStackTrace();
			} */
			boolean flag = false;
			for (int i=0; i<100; i++) {
				try {
					socket.connect(addr, timeout);
					Boolean b = (Boolean) talk(socket, Request.PING, null, null);
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
	 * asks DataBase servers about some info from Data Base
	 * @param SQL query (String)
	 * @return ResultSet from DBServers 
	 */
	
	public ResultSet getInfo(String query) {
		
		for (InetSocketAddress addr : dbAdresses) {
			Socket socket = new Socket();
			/*SSLSocket socket = null;
			try {
				socket = (SSLSocket) factory.createSocket();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} */
			for (int i=0; i<100; i++) {
				try {
					socket.connect(addr, timeout);
					ResultSet b = (ResultSet) talk(socket, Request.INFO, query, null);
					if (b != null) {
						return b;
					}
					
				} catch (Exception e) {
					System.out.println("error in connection"); //DEBUG
					e.printStackTrace(); //DEBUG
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
