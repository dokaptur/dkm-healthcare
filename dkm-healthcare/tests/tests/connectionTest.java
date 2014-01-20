package tests;

//import static org.junit.Assert.*;



import org.junit.Test;
import servers.NServer;

public class connectionTest {

	@Test
	public void test() {
		
	}
	
	@Test
	public void mailtest() {
		NServer ns = new NServer();
		NServer.Notifier not = ns.new Notifier("select email,  imie, nazwisko from osoby where email = dokaptur@gmail.com", NServer.NotifyType.HISTORY);
		not.run();
	}

}
