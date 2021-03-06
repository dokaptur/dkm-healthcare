package others;


import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Class with configuration
 * @author dudu
 *
 */

public class Config {
	
	/**
	 * addresses of servers
	 */
	public InetSocketAddress BD1addr;
	public InetSocketAddress BD2addr;
	public InetSocketAddress Naddr;
	public InetSocketAddress NGaddr;
	
	/**
	 * password to application email account
	 */
	public String password;
	
	/**
	 * instances of Calendar classes - to keep fixed hours
	 */
	public Calendar midnight = Calendar.getInstance();
	public Calendar pm4 = Calendar.getInstance();
	

	/**
	 * administrator's email address
	 */
	
	public static String adminMail = "dokaptur@gmail.com";
	
	/**
	 * Method to set appropriate hours in Calendar classes
	 */
	void setCallendar() {
		midnight.set(Calendar.HOUR, 1);
		midnight.set(Calendar.AM_PM, Calendar.AM);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		
		pm4.set(Calendar.HOUR, 4);
		pm4.set(Calendar.AM_PM, Calendar.PM);
		pm4.set(Calendar.MINUTE, 0);
		pm4.set(Calendar.SECOND, 0);
	}
	
	/**
	 * Constructor 
	 * fills runs setCalendar and fillAdresses methods
	 */
	public Config() {
		//fillAdresses();
		setCallendar();
		
		//tmp -before reorganizing!!!
		password = "bazysieci";
		BD1addr = new InetSocketAddress("localhost",2004);
		BD2addr = new InetSocketAddress("localhost", 2005);
		Naddr = new InetSocketAddress("localhost", 2006);
		NGaddr = new InetSocketAddress("localhost", 2007);
	}
	
	/**
	 * Method to read and fill addresses and password from configuration file
	 */
	void fillAdresses() {
		try {
			//FileInputStream fis = new FileInputStream(System.getProperty("config"));
			Scanner sc = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("config"));
			while (sc.hasNext()) {
				String s = sc.next();
				String host;
				int port;
				if (sc.equals("password")) {
					password = sc.next();
				} else {
					host = sc.next();
					port = sc.nextInt();
					if (s.equals("NServer")) {
						Naddr = new InetSocketAddress(host, port);
					} else if (s.equals("NGServer")) {
						NGaddr = new InetSocketAddress(host, port);
					} else if (s.equals("BD1")) {
						BD1addr = new InetSocketAddress(host, port);
					} else if (s.equals("BD2")) {
						BD2addr = new InetSocketAddress(host, port);
					}
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
