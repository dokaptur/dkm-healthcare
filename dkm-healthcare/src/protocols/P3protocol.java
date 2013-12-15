package protocols;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * An application-level protocol for communication between notifier server and data basis servers.
 * @author dudu
 */

public class P3protocol {
	
	/**
	 * adresses of DataBasis servers
	 */
	
	ArrayList<InetSocketAddress> serverAdresses;
	int timeout = 1000;
	
	/**
	 * Class which includes email adress of Patient and name of medicine.
	 * Used to send notifications
	 * Implements Serializable to enable writing to ObjectOutputStream
	 * @author dudu
	 *
	 */
	
	public static class MyNotification implements Serializable {
		
		private static final long serialVersionUID = -846304785237772868L;
		String email;
		String name;
		String presciption;
		
		public MyNotification(String email, String name, String prescription) {
			this.email = email;
			this.name = name;
			this.presciption = prescription;
		}
	}
	
	public static enum QueryType {
		EXPIRED, LEFT5;
	}
	
	public P3protocol(ArrayList<InetSocketAddress> serverAdresses) {
		this.serverAdresses = serverAdresses;
	}
	
	/**
	 * checks if server is available
	 * @param socket adress of server we want to "ping" 
	 * @return "availability"
	 */
	
	public boolean pingServer(InetSocketAddress server) {
		Socket socket = new Socket();
		for (int i=0; i<100; i++) {
			try {
				socket.connect(server, timeout);
				PrintWriter os = new PrintWriter(socket.getOutputStream());
				Scanner is = new Scanner (socket.getInputStream());
				
				os.println("P3");
				os.flush();
				String s = is.next();
				if (s.equals("I don't know you. Bye!")) {
					is.close(); 
					socket.close();
					return false;
				}
				
				
				os.println("ping");
				os.flush();
				is.next();
				
				os.close(); is.close();
				socket.close();
				return true;
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (Exception e1) {
				}
			}
		}
		return false;
	}
	
	/**
	 * asks DataBasis server about which prescriptions we should send notifications (together with email adresses and names of patients) 
	 * @param list where we want to store answer from server and type of query
	 * @return adress of server we managed to connect with
	 */
	
	@SuppressWarnings("unchecked")
	public InetSocketAddress getPrescriptions (ArrayList<MyNotification> nots, QueryType type) {
		Socket socket = new Socket();
		for (InetSocketAddress adr : serverAdresses) {
			for (int i=0; i<100; i++) {
				
				try {
					socket.connect(adr, timeout);
					PrintWriter os = new PrintWriter(socket.getOutputStream());
					ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
					
					os.println("P3");
					os.flush();
					Object obj = is.readObject();
					if (((String) obj).equals("I don't know you. Bye!")) {
						socket.close();
						break;
						// powiadom admina że coś się zepsuło. Tego serwera nie ma co dalej próbować
					}
					
					os.println("give me info");
					os.flush();
					is.readObject();
					
					os.println(type.toString());
					os.flush();
					nots = (ArrayList<MyNotification>) is.readObject();
					
					os.println("got it");
					os.flush();
					is.readObject();
					
					os.close(); is.close();
					socket.close();
					return adr;
				} catch (Exception e) {
					try {
						Thread.sleep(3000);
					} catch (Exception e1) {
					}
					continue;
				}
				
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("tmp");
	}

}
