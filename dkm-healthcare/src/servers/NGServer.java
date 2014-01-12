package servers;

import java.util.*;

import others.Config;
import protocols.P4protocol;
import protocols.P4protocol.P4Site;
import servers.NServer.NotifyType;


public class NGServer {
	
	Config config;
	NServer ns;
	
	public NGServer() {
		config = new Config();
		ns = new NServer();
	}
	
	public class PingDBChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG, config);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.Pinger p = ns.new Pinger();
				p.run();
				ns.NotifyAdmin(P4protocol.nserveraddr);
			}
		}
		
	}
	
	public class PrecriptionChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG, config);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.Notifier l5 = ns.new Notifier(ns.left5Query, NotifyType.LEFT5);
				NServer.Notifier ex = ns.new Notifier(ns.expireQuery, NotifyType.EXPIRE);
				l5.run();
				ex.run();
				ns.NotifyAdmin(P4protocol.nserveraddr);
			}
			
		}
		
	}
	
	public class HistoryChecker extends TimerTask {

		@Override
		public void run() {
			P4protocol p4 = new P4protocol(P4Site.NG, config);
			boolean b = p4.PingNServer();
			if (!b) {
				NServer.Notifier p = ns.new Notifier(ns.historyQuery, NotifyType.HISTORY);
				p.run();
				ns.NotifyAdmin(P4protocol.nserveraddr);
			}
			
		}
		
	}
	
	public class ImAlive extends TimerTask {

		@Override
		public void run() {
			ns.SendMail("dkm.dkmhealthcare@gmail.com", "Here is NGServer! I'm ok!");
		}
		
	}
	
	public static void main(String[] args) {
		
		NGServer ng = new NGServer();
		Calendar beforeMid = (Calendar) ng.config.midnight.clone();
		Calendar beforepm4 = (Calendar) ng.config.pm4.clone();
		beforeMid.add(Calendar.MINUTE, -5);
		beforepm4.add(Calendar.MINUTE, -5);
		
		(new Timer()).scheduleAtFixedRate(ng.new PingDBChecker(), beforeMid.getTime(), 3600 * 1000);
		
		(new Timer()).scheduleAtFixedRate(ng.new HistoryChecker(), beforepm4.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(ng.new PrecriptionChecker(), beforeMid.getTime(), 3600 * 1000 * 12);
		
		(new Timer()).scheduleAtFixedRate(ng.new ImAlive(), ng.config.midnight.getTime(), 3600 * 1000 * 12); 
	}
	
	

}