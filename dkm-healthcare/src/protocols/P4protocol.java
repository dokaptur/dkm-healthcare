package protocols;

import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;


public class P4protocol {
	
	public static enum P4Site {
		N, NG;
	}
	
	public P4Site site;
	
	public static InetSocketAddress nserveraddr = new InetSocketAddress("localhost", 2006);
	
	public P4protocol (P4Site site) {
		this.site = site;
	}
	
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
