package servers;

import java.io.*;
import java.net.*;

import others.Config;
import protocols.P3protocol;
import protocols.P4protocol;
import protocols.P3protocol.Site;
import protocols.P4protocol.P4Site;

/**
 * Class to use when someone connects to Server. Implements Runnable to start it in new thread.
 * @author dudu
 *
 */

public class ServerSocketThread implements Runnable {
	
	/**
	 * enum to determine server
	 * @author dudu
	 *
	 */
	
	public static enum ServerType {
		N, NG, DB1, DB2;
	}
	
	/**
	 * socket used in communication
	 */
	Socket socket;
	
	/**
	 * an instance of DBServer. 
	 */
	BDServer bd;
	
	/**
	 * type of server
	 */
	ServerType type;
	
	/**
	 * An instance of Config class
	 */
	Config config;
	
	
	
	/**
	 * Constructor for NServer and NGServer
	 * @param socket (socket already connected)
	 * @param type (type of server)
	 * @param config
	 */
	public ServerSocketThread(Socket socket, ServerType type, Config config) {
		this.config = config;
		this.type = type;
		this.socket = socket;
	}
	
	/**
	 * Constructor for DBServers
	 * @param socket (socket already connected)
	 * @param type (type of server)
	 * @param config
	 * @param dbServer
	 */
	public ServerSocketThread(Socket socket, ServerType type, Config config, BDServer bd) {
		this.config = config;
		this.type = type;
		this.socket = socket;
		this.bd = bd;
	}
	
	/**
	 * method from Runnable interface - gets message from sockets and opens  appropriate protocol 
	 */

	@Override
	public void run() {
		try {
			PrintWriter wr = new PrintWriter(socket.getOutputStream());
			BufferedReader sc = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String s = sc.readLine();
			System.out.println(s);
			
			/**
			 * if someone talk to us with P3 protocol we must be DBServer
			 */
			
			if (s.equals("P3")) {
				P3protocol protocol = new P3protocol(Site.DB, config);
				protocol.talk(socket, null, null, bd);
			} 
			else if (s.equals("P1")) {
				
			} 
			else if (s.equals("P2")) {
				
			} 
			else if (s.equals("P4")) {
				P4protocol p4 = new P4protocol(P4Site.N, config);
				p4.talk(socket);
			} 
			else {
				wr.write("Protocol error! Bye!");
			}
			wr.close(); sc.close();
			socket.close();
		} catch (Exception e) {
			
		}
		
	}

}
