package protocols;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import others.Config;

/**
 * An application-level protocol for communication between NSever (notifications server) and NGServer (NServer's Guardian).
 * @author dudu
 *
 */

public class P4protocol {
	
	/**
	 * enumerator to determine if instance of P4protocol class has been created by NServer or NGServer
	 * @author dudu
	 *
	 */
	
	public static enum P4Site {
		N, NG;
	}
	
	/**
	 * field of P4Site
	 */
	public P4Site site;
	
	/**
	 * address of NServer
	 */
	public static InetSocketAddress nserveraddr; 
	
	/**
	 * instance of config class
	 */
	Config config;
	
	/**
	 * constructor
	 * @param site (we want to know of we are in NServer or NGServer
	 */
	public P4protocol (P4Site site, Config config) {
		this.site = site;
		this.config = config;
		nserveraddr = config.Naddr;
	}
	
	/**
	 * The most important function of this class.
	 * It implements how servers talk to each other.
	 * @param socket
	 * @return true if everything was ok. False otherwise
	 * @throws Exception
	 */
	
	public boolean talk(Socket socket) throws Exception {
		PrintWriter wr = new PrintWriter (socket.getOutputStream());
		BufferedReader sc = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		if (site == P4Site.N) {
			wr.println("P4 Protocol. What do you want?"); wr.flush();
			if (sc.readLine().equals("Ping!")) {
				wr.println("Pong!"); wr.flush();
				wr.close(); sc.close();
				return true;
			} 
			wr.close(); sc.close();
			return false;
		} else { // site == P4Site.NG
			wr.println("P4"); wr.flush();
			if (sc.readLine().equals("P4 Protocol. What do you want?")) {
				wr.println("Ping!"); wr.flush();
				if (sc.readLine().equals("Pong!")) {
					wr.close(); sc.close();
					return true;
				}
			}
			sc.close(); wr.close();
			return false;
		}
	}
	
	/**
	 * Function to ping NServers. It creates socket and starts to talk.
	 * Used only by instances of class created in NServer.
	 * @return
	 */
	
	public boolean PingNServer() {
		int timeout = 1000;
		Socket socket = new Socket();
		boolean b = false;
		for (int i=0; i<100; i++) {
			try {
				socket.connect(nserveraddr, timeout);
				b = talk(socket);
				if (b) break;
				
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (Exception e1) {
				}
			}
		}
		return b;
	}

}
