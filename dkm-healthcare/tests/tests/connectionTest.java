package tests;

//import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Test;

import others.Config;
import others.Doctor;
import protocols.P3protocol;
import protocols.P3protocol.Site;
import servers.NServer;

public class connectionTest {

	@Test
	public void test() {
		Config config = new Config();
		P3protocol p3 = new P3protocol(Site.Notify, config);
		ResultSet rs = p3.getInfo("select * from zabiegi");
		Doctor dr = new Doctor("dupa");
		dr.printResult(rs, 2);
	}
	
	@Test
	public void mailtest() {
		NServer ns = new NServer();
		NServer.Notifier not = ns.new Notifier("select email,  imie, nazwisko from osoby where email = dokaptur@gmail.com", NServer.NotifyType.HISTORY);
		not.run();
	}

}
