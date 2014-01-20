package protocols;

import java.io.*;
import java.net.*;
//import java.sql.*;
import java.util.*;

//import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

//import com.sun.rowset.CachedRowSetImpl;


import others.Config;
import servers.BDServer;

/**
 * An application-level protocol for communication between user and data basis
 * servers.
 * 
 * @author retax
 */

public class P2protocol {

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
	 * timeout for connection
	 */
	int timeout = 1000;

	/**
	 * enum to differentiate between DBs
	 */
	public static enum Site {
		DBInit, DBRec;
	}

	/**
	 * enum to determine what do we want from DBServer
	 */
	public static enum Request {
		PING, GETFILE, UPDATE;
	}

	Site site;
	BDServer bd;

	/**
	 * P1protocol constructor; we fill in addresses of servers from config
	 * object
	 * 
	 * @param config
	 *            - list of database servers addresses
	 * @param site
	 *            - which DB we are
	 * @param bd - an instance of DBServer we are in
	 */
	public P2protocol(Config config, Site site, BDServer bd) {
		this.config = config;
		this.site = site;
		this.bd = bd;

		dbAdresses.add(config.BD1addr);
		dbAdresses.add(config.BD2addr);
	}

	/**
	 * The most important method. Here is implemented whole communication.
	 * 
	 * @param socket
	 *            - for connection between Data Base and user
	 * @param request
	 *            - for connection between Data Base and user
	 * @param query
	 *            - argument of UPDATE request
	 * @param con
	 *            - for connection between Data Bases
	 * @return null if we are in DBServer or if error occurs. Otherwise Boolean
	 *         while pinging and ResultSet while asking DB about some info.
	 * 
	 * @throws Exception
	 */
	public Object talk(Socket socket, Request request, String query) throws Exception {
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		PrintWriter wr = new PrintWriter(os);
		BufferedReader sc = new BufferedReader(new InputStreamReader(is));

		if (site == Site.DBRec) {

			wr.println("P2 Protocol. What do you want?");
			wr.flush();

			String ans = sc.readLine(); System.out.println(ans);
			if (ans.equals("Ping!")) {
				wr.println("Pong!");
				wr.flush();
				sc.close();
				wr.close();
				return null;
			}

			if (ans.equals("Update your DB!")) {
				wr.println("Ok, give me query");
				wr.flush();
				String query1 = sc.readLine();

				bd.executeUpdate(query1);
				wr.println("OK");
			}

			if (ans.equals("Download!")) {
				FileReader in = new FileReader("//log");
				BufferedReader br = new BufferedReader(in);
				String s = br.readLine();
				while (s != null) {
					wr.println(s);
				}
				wr.println("FINISH");
				br.close();
				
			}

			if (ans.equals("Halo")) {
				wr.println("I'm alive");
				wr.flush();

				String ans2 = sc.readLine();
				bd.executeUpdate(ans2);
				wr.println("OK");
			}

			sc.close();
			wr.close();
			//throw (new ProtocolException());

		} else { // site = DBinit
			wr.println("P2");
			wr.flush();
			String s = sc.readLine(); System.out.println(s);
			if (!s.equals("P2 Protocol. What do you want?")) {
				sc.close();
				wr.close();
				return null;
			}

			if (request == Request.PING) {
				wr.println("Ping!");
				wr.flush();
				s = sc.readLine(); System.out.println(s);
				if (!s.equals("Pong!")) {
					sc.close();
					wr.close();
					return null;
				}
				sc.close();
				wr.close();
				return new Boolean(true);
			}

			if (request == Request.UPDATE) {
				wr.println("Update your DB!");
				wr.flush();
				s = sc.readLine(); System.out.println(s);
				if (s.equals("Ok, give me query")) {
					wr.println(query);
					wr.flush();
					s = sc.readLine(); System.out.println(s);
					
				} else {
					boolean succeeded = false;

					for (int i = 0; i < 100; i++) {
						wr.print("Halo!");
						wr.flush();
						s = sc.readLine();
						if (s.equals("I'm alive!")) {
							wr.println(query);
							succeeded = true;
							s = sc.readLine();
							break;
						} else {
							try {
								Thread.sleep(3000);
							} catch (Exception e) {

							}
						}
					}

					if (!succeeded) {
						FileOutputStream in = new FileOutputStream("//log");
						PrintWriter pw = new PrintWriter(in);

						pw.println(query);
						pw.close();
					}
				}
				wr.close();
				sc.close();
				if (s.equals("OK!")) {
					return Boolean.TRUE;
				} else
					return Boolean.FALSE;

			}

			if (request == Request.GETFILE) {
				wr.println("Download!");
				wr.flush();
				s  = sc.readLine();
				while (! s.equals("FINISH")) {
					bd.executeUpdate(s);
					s = sc.readLine();
				}
				return true;
			}
		}

		return null;
	}
	
	public boolean initTalk(Request request, String query, InetSocketAddress address) {
		Socket socket = new Socket();
		for (int i=0; i<100; i++) {
			try {
				socket.connect(address);
				talk(socket, request, query);
				return true;
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
			}
		}
		return false;
		
	}
}
