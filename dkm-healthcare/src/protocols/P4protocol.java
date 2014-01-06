package protocols;

import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

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
	public static InetSocketAddress nserveraddr = new InetSocketAddress("localhost", 2006);
	
	/**
	 * constructor
	 * @param site (we want to know of we are in NServer or NGServer
	 */
	public P4protocol (P4Site site) {
		this.site = site;
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
		Scanner sc = new Scanner (socket.getInputStream());
		if (site == P4Site.N) {
			wr.write("P4 Protocol. What do you want?");
			if (sc.nextLine().equals("Ping!")) {
				wr.write("Pong!");
				wr.close(); sc.close();
				return true;
			} 
			wr.close(); sc.close();
			return false;
		} else { // site == P4Site.NG
			wr.write("P4");
			if (sc.nextLine().equals("P4 Protocol. What do you want?")) {
				wr.write("Ping!");
				if (sc.nextLine().equals("Pong!")) {
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
