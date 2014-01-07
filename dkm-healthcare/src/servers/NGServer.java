package servers;

import java.util.*;

import protocols.P4protocol;
import protocols.P4protocol.P4Site;


public class NGServer {
	
	public static class PingDBChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.Pinger p = new NServer.Pinger();
				p.run();
				NServer.NotifyAdmin(P4protocol.nserveraddr);
			}
		}
		
	}
	
	public static class PrecriptionChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.Left5_Notifier l5 = new NServer.Left5_Notifier();
				NServer.Expire_Notifier ex = new NServer.Expire_Notifier();
				l5.run();
				ex.run();
				NServer.NotifyAdmin(P4protocol.nserveraddr);
			}
			
		}
		
	}
	
	public static class HistoryChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.History_Notifier p = new NServer.History_Notifier();
				p.run();
				NServer.NotifyAdmin(P4protocol.nserveraddr);
			}
			
		}
		
	}
	
	public static class ImAlive extends TimerTask {

		@Override
		public void run() {
			NServer.SendMail("dkm.dkmhealthcare@gmail.com", "Here is NGServer! I'm ok!");
		}
		
	}
	
	public static void main() {
		Calendar beforeMid = (Calendar) NServer.midnight.clone();
		Calendar beforepm4 = (Calendar) NServer.pm4.clone();
		beforeMid.add(Calendar.MINUTE, -5);
		beforepm4.add(Calendar.MINUTE, -5);
		
		(new Timer()).scheduleAtFixedRate(new PingDBChecker(), beforeMid.getTime(), 3600 * 1000);
		
		(new Timer()).scheduleAtFixedRate(new HistoryChecker(), beforepm4.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(new PrecriptionChecker(), beforeMid.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(new ImAlive(), NServer.midnight.getTime(), 3600 * 1000 * 12);
	}
	
	

}