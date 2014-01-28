package others;

import java.sql.ResultSet;
import java.util.Scanner;

import servers.Client;

/**
 * A class to work in pharmacist mode. It is created in Client class if client chose to work as pharmasict. 
 * @author dudu
 *
 */

public class Pharmacist {
	
	/**
	 * pesel of client
	 */
	
	String pesel;
	
	Client client;
	
	/**
	 * constructor
	 * @param pesel
	 * @param client
	 */
	public Pharmacist (String pesel, Client client) {
		this.pesel = pesel;
		this.client = client;
	}
	/**
	 * class to "realize" prescription. Sends query to DBServer asking to modify some data (using Client.getRSbyP1 method)
	 * @param sc
	 */
	private void realizePrecsription(Scanner sc) {
		System.out.println("Wprowadź numer recepty:\n");
		String nr = sc.next();
		String query = "update recepty set zrealizowana = true, zrealizowana_przez = '" + pesel + 
				"' where numer = " + nr + ";";
		ResultSet rs = client.getRSbyP1("select zrealizowana from recepty where numer = " + nr + ";");
		
		try {
			if (rs.next() && rs.getBoolean(1)) {
			} else {
				System.out.println("\nTa recepta już wcześniej została zrealizowana!\n");
			}
			
		} catch (Exception e) {
			System.out.println("\nNastąpił błąd!\n");
		}
		
		if (client.updateBDbyP1(query)) {
			System.out.println("\nRecepta została zrealizowana.\n");
		} else {
			System.out.println("\nNastępił błąd!\n");
		}
		
	}
	
	/**
	 * main "loop function" to work in pharmacist mode
	 */
	public void perform() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Aby zrealizować receptę, wprowadź 1");
			System.out.println("Aby zakończyć pracę w trybie aptekarza, wprowadź 2");
			int i = sc.nextInt();
			switch (i) {
			case 1:
				realizePrecsription(sc);
				break;
			case 2:
				System.out.println("Dziekujemy za pracę w trybie aptekarza!");
				sc.close();
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
			
		}
	}

}
