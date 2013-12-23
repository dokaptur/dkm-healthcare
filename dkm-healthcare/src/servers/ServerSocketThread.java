package servers;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import protocols.P3protocol;
import protocols.P3protocol.Site;

/**
 * Class to use when someone connects to Server. Implements Runnable to start it in new thread.
 * @author dudu
 *
 */

public class ServerSocketThread implements Runnable {
	
	/**
	 * enum to determine of server
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
	 * connection with Data Basis. Not null only for DBServers. 
	 */
	Connection con;
	
	/**
	 * type of server
	 */
	ServerType type;
	
	/**
	 * Constructor for NServer and NGServer
	 * @param socket
	 */
	
	public ServerSocketThread(Socket socket, ServerType type) {
		this.type = type;
		this.socket = socket;
	}
	
	/**
	 * Constructor for DBServers
	 * @param socket
	 * @param con
	 */
	public ServerSocketThread(Socket socket, ServerType type, Connection con) {
		this.type = type;
		this.socket = socket;
		this.con = con;
	}
	
	/**
	 * method from Runnable interface
	 */

	@Override
	public void run() {
		try {
			PrintWriter wr = new PrintWriter(socket.getOutputStream());
			Scanner sc = new Scanner(socket.getInputStream());
			String s = sc.nextLine();
			
			/**
			 * if someone talk to us with P3 protocol we must be DBServer
			 */
			
			if (s.equals("P3")) {
				P3protocol protocol = new P3protocol(Site.DB);
				protocol.talk(socket, null, con);
			} 
			else if (s.equals("P1")) {
				
			} 
			else if (s.equals("P2")) {
				
			} 
			else if (s.equals("P4")) {
				
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
