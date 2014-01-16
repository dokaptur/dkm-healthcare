package others;


import java.util.Scanner;

import servers.Client;

/**
 * A class to work in patient mode. It is created in Client class if client chose to work as patient. 
 * @author michal
 *
 */
public class Patient {
	
	/**
	 * pesel of client
	 */
	private String pesel;
	private Scanner scan = new Scanner(System.in);

	/**
	 * constructor
	 * @param pesel
	 */
	public Patient(String pesel) {
		this.pesel = pesel;
	}
	
	/**
	 * Main function. User can choose what he/she would like to do.
	 * 
	 */
	public void perform() {
		while(true){
			System.out.println( "W czym moge sluzyc?\n"+
					"Aby sciagnac historie choroby wcisnij 1;\n"+
					"Aby wyswietlic informacje o tobie wcisnij 2;\n"+
					"Aby wyswietlic recepty wcisnij 3;\n"+
					"Aby wyswietlic skierowania wcisnij 4;\n"+
					"Aby znalezc informacje o wykonywanych zabiegach wcisnij 5;\n"+
					"Aby znalezc informacje o specjalistach wcisniej 6;\n"+
					"Aby wyswietlic informacje o twoim lekarzu rodzinnym wcisniej 7;\n"+
					"Aby zakonczyc wcisnij 0."
					);
			switch(scan.nextInt()){
			case 1: disease(); break;
			case 2: info(); break;
			case 3: recepts_info(); break;
			case 4: referrals_info(); break;
			case 5: operations_info(); break;
			case 6: specialists_info(); break;
			case 7: familyDoc(); break;
			case 0: byebye(); return;
			default: System.out.println("wylacznie liczby ze zbioru {0,...,7} dopuszczalnymi sa. Sprobuj ponownie!");
			}
		}
	}
	/*
	/**
	 * It connects to database, executes query and returns result as a ResultSet
	 * @param query
	 * @return
	 
	private ResultSet executeQuery(String query){
		String  url="jdbc:postgresql:michal",
				user="med",
				passwd="med";
		try{
		    Class.forName("org.postgresql.Driver");
		    } catch (ClassNotFoundException cnfe){
		      System.out.println("Could not find the JDBC driver!");
		      System.exit(1);
		    }
		Connection conn = null;
		ResultSet result = null;
		try {
		    conn = DriverManager.getConnection(url, user, passwd);
		    Statement stat = conn.createStatement();
		    stat.execute("SET search_path TO 'dkm-healthcare'");
//		    System.out.println(query);
		    result = stat.executeQuery(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Could not close connection");
			}
		}
		return result;
	} */

	/**
	 * It downloads patient's history to path specified by user.
	 */
	private void disease() {
		System.out.println("\nPodaj sciezke do zapisu pliku\n");
		String path=scan.next(".*[a-zA-Z\\/]*");
		download(path);
	}
	
	/**
	 * Manages downloading.
	 * @param path
	 */
	private void download(String path){}

	/**
	 * It shows all information about user, including basic info, drugs, referrals etc
	 */
	private void info() {
		//dane podstawowe
		System.out.println("\nInformacje podstawowe");
		System.out.println("imie, nazwisko, pesel, data_urodzenia, adres");
		String query="SELECT imie, nazwisko, pesel, data_urodzenia, adres FROM osoby WHERE pesel='"+pesel+"';";
		Client.printResult(Client.getRSbyP1(query), true, scan);

		//informacje z tabeli osoby_info
		System.out.println("\nInformacje dodatkowe");
		System.out.println("imie, nazwisko, info, historia modyfikacja");
		query="SELECT imie, nazwisko, info, historia_modyfikacja FROM "+
			  "osoby o JOIN osoby_info oi ON lekarz_rodzinny=o.pesel  WHERE oi.pesel='"+pesel+"';";
		Client.printResult(Client.getRSbyP1(query), true, scan);

		//informacje o alergiach
		System.out.println("\nAlergie");
		query="SELECT alergia FROM osoby_alergie WHERE pesel='"+pesel+"';";
		Client.printResult(Client.getRSbyP1(query), true, scan);

		//informacje o lekach
		System.out.println("\nLeki");
		query="SELECT lek FROM osoby_leki WHERE pesel='"+pesel+"';";
		Client.printResult(Client.getRSbyP1(query), true, scan);

		//informacje o specjalistach
		System.out.println("\nInformacje o specjalistach");
		System.out.println("imie, nazwisko");
		query="SELECT s.imie, s.nazwisko FROM osoby o NATURAL JOIN pacjenci_specjalisci join osoby s on id_lekarz=s.pesel WHERE o.pesel='"+pesel+"';";
		Client.printResult(Client.getRSbyP1(query), true, scan);

	}

	/**
	 * It displays details about user's prescriptions.
	 */
	private void recepts_info() {
		System.out.println("\nnumer, lek, data_waznosci, zrealizowana");
		String query="SELECT numer, lek, data_waznosci, zrealizowana FROM recepty "+
					 "WHERE pesel='"+pesel+"' ORDER BY 4 DESC;";
		Client.printResult(Client.getRSbyP1(query), false, scan);
	}
	
	/**
	 * It displays details about user's referrals.
	 */
	private void referrals_info() {
		System.out.println("\nnumer, nazwa, zrealizowany");
		String query="SELECT numer, nazwa, zrealizowany FROM skierowania NATURAL LEFT JOIN zabiegi "+
					 "WHERE pesel='"+pesel+"' ORDER BY 3 DESC, 1;";
		Client.printResult(Client.getRSbyP1(query), false, scan);
	}

	/**
	 * User can find info about given operations in given city.
	 */
	private void operations_info() {
		String query,miasto,zabieg;
		System.out.println("podaj miasto lub wpisz menu aby wrocic do menu glownego");
		do {
			if ( (miasto=scan.nextLine()).equals("menu") ) return;
			if (miasto.length()<=1) continue;
			break;
		} while(true);
		System.out.println("podaj nazwe zabiegu lub wpisz menu aby wrocic do menu glownego");
		do {
			if ( (zabieg=scan.nextLine()).equals("menu") ) return;
			if (zabieg.length()<=1) continue;
			break;
		} while(true);		query="SELECT p.nazwa, typ, adres, nr_tel FROM placowki p NATURAL JOIN placowki_zabiegi "+
					 "JOIN zabiegi z using (id_zabieg) WHERE miasto='"+miasto+"' AND z.nazwa='"+zabieg+"';";
		System.out.println("\nnazwa, typ, adres, nr_tel");
		Client.printResult(Client.getRSbyP1(query), false, scan);
	}

	/**
	 * User can find info about given specialists in given city.
	 */
	private void specialists_info() {
		String query,miasto,specjalista;
		System.out.println("podaj miasto lub wpisz menu aby wrocic do menu glownego");
		do {
			if ( (miasto=scan.nextLine()).equals("menu") ) return;
			if (miasto.length()<=1) continue;
			break;
		} while(true);		System.out.println("podaj nazwe specjalisty lub wpisz menu aby wrocic do menu glownego");
		do {
			if ( (specjalista=scan.nextLine()).equals("menu") ) return;
			if (specjalista.length()<=1) continue;
			break;
		} while(true);		query="SELECT p.nazwa, typ, adres, nr_tel FROM placowki p NATURAL JOIN lekarze_placowki "+
					 "NATURAL JOIN lekarze_specjalizacje JOIN specjalizacje s using (id_spec) WHERE miasto='"+miasto+
					 "' AND s.nazwa='"+specjalista+"';";
		System.out.println("\nnazwa, typ, adres, nr_tel");
		Client.printResult(Client.getRSbyP1(query), false, scan);
	}

	/**
	 * It displays info about user's family doc.
	 */
	private void familyDoc() {
		String query="SELECT imie, nazwisko, nazwa, p.adres, nr_tel "+
					 "FROM osoby_info oi JOIN lekarze_placowki on lekarz_rodzinny=id_lekarz NATURAL JOIN placowki p "+
					 "JOIN osoby ol ON id_lekarz=ol.pesel "+
					 "WHERE oi.pesel='"+pesel+"' AND typ='przychodnia'";

		System.out.println("imie, nazwisko, nazwa, adres, nr_tel");
		Client.printResult(Client.getRSbyP1(query), false, scan);
	}

	
	/**
	 * It says bay bay.
	 */
	private void byebye() {
		System.out.println("good night and good luck");
	}
}
