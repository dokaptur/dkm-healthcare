package others;


import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Scanner;

public class Config {
	public InetSocketAddress BD1addr;
	public InetSocketAddress BD2addr;
	public InetSocketAddress Naddr;
	public InetSocketAddress NGaddr;
	
	public String password;
	
	public Calendar midnight;
	public Calendar pm4;
	

	/**
	 * administrator's email address
	 */
	
	public static String adminMail = "dokaptur@gmail.com";
	
	
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
	
	public Config() {
		fillAdresses();
		setCallendar();
	}
	
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
