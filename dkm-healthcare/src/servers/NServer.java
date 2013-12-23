package servers;

import java.net.*;
import java.util.ArrayList;

import protocols.P3protocol;
import protocols.P3protocol.Site;
import servers.ServerSocketThread.ServerType;

/**
 * Class for Notification Server
 * @author dudu
 *
 */

public class NServer {
	
	public static class Pinger implements Runnable {

		@Override
		public void run() {
			P3protocol protocol = new P3protocol(Site.Notify);
			while (true) {
				ArrayList<Boolean> list = protocol.pingServers();
				for (int i=0; i<protocol.dbAdresses.size(); i++) {
					if (list.get(i) == Boolean.FALSE) {
						NServer.SendMail(); // tmp : arguments!!!
						// send PING mails to admin
					}
				}
				try {
					Thread.sleep(3600*1000);
				} catch (InterruptedException e) {
					
				}
				
			}
		}
	}
	
	public static class History_Notifier implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class Left5_Notifier implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class Expire_Notifier implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static boolean SendMail() {
		return true;
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Pinger ping = new Pinger();
		ping.run();
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
