package others;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class Patient {
	private String pesel, passwd;
	
	public Patient(String pesel, String passwd) {
		this.pesel = pesel;
		this.passwd = passwd;
	}
	
	public void perform() {
		Scanner scan = new Scanner(System.in);
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
			case 1: disease(scan); break;
			case 2: info(); break;
			case 3: recepts_info(); break;
			case 4: referrals_info(); break;
			case 5: operations_info(scan); break;
			case 6: specialists_info(scan); break;
			case 7: familyDoc(); break;
			case 0: byebye(); break;
			default: System.out.println("wylacznie liczby ze zbioru {0,...,7} dopuszczalnymi sa. Sprobuj ponownie!");
			}
		}
	}

	private ResultSet executeQuery(String query){
		String  url="jdbc:postgresql:michal",
				user="michal",
				passwd="michal";
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
		    result = stat.executeQuery(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       System.exit(1);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Could not close connection");
			}
		}
		return result;
	}

	private void disease(Scanner scan) {
		System.out.println("Podaj sciezke do zapisu pliku\n");
		String path=scan.nextLine();
		//TODO download file to path 
	}

	private void info() {
		//dane podstawowe
		String query="SELECT imie, nazwisko, pesel, data_urodzenia, adres FROM osoby WHERE pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		//informacje czy jest lekarzem/aptekarzem
		query="SELECT aptekarz FROM osoby WHERE pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		query="SELECT id_lekarz FROM lekarze WHERE id_lekarz='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		//informacje z tabeli osoby_info
		query="SELECT imie, nazwisko, info, historia_modyfikacji FROM"+
			  "osoby o JOIN osoby_info oi ON lekarz_rodzinny=o.pesel  WHERE oi.pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		//informacje o alergiach
		query="SELECT alergia FROM osoby_alergie WHERE pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		//informacje o lekach
		query="SELECT lek FROM osoby_leki WHERE pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

		//informacje o specjalistach
		query="SELECT imie, nazwisko FROM osoby o NATURAL JOIN pacjenci_specjalisci join osoby s on id_lekarz=s.pesel WHERE o.pesel='"+pesel+"';";
		//TODO
		printResult(executeQuery(query));

	}

	private void recepts_info() {
		String query="SELECT numer, lek, data_waznosci, zrealizowana FROM recepty"+
					 "WHERE pesel='"+pesel+"' ORDER BY 4 DESC;";
		//TODO
		printResult(executeQuery(query));
	}
	
	private void referrals_info() {
		String query="SELECT numer, nazwa, zrealizowany FROM skierowania NATURAL LEFT JOIN zabiegi"+
					 "WHERE pesel='"+pesel+"' ORDER BY 3 DESC, 1;";
		//TODO
		printResult(executeQuery(query));
	}

	private void operations_info(Scanner scan) {
		String query,miasto,zabieg;
		System.out.println("podaj miasto lub wpisz exit aby wrocic do menu glownego");
		miasto=scan.nextLine();
		System.out.println("podaj zabieg lub wpisz exit aby wrocic do menu glownego");
		zabieg=scan.nextLine();
		query="SELECT p.nazwa, typ, adres, nr_tel FROM placowki p NATURAL JOIN placowki_zabiegi"+
					 "NATURAL JOIN zabiegi z WHERE miasto='"+miasto+"' AND z.nazwa='"+zabieg+"';";
		//TODO
		printResult(executeQuery(query));
	}

	
	private void specialists_info(Scanner scan) {
		String query,miasto,specjalista;
		System.out.println("podaj miasto lub wpisz exit aby wrocic do menu glownego");
		miasto=scan.nextLine();
		System.out.println("podaj nazwe specjalisty lub wpisz exit aby wrocic do menu glownego");
		specjalista=scan.nextLine();
		query="SELECT p.nazwa, typ, adres, nr_tel FROM placowki p NATURAL JOIN lekarze_placowki"+
					 "NATURAL JOIN lekarze_specjalizacje NATURAL JOIN specjalizacje s WHERE miasto='"+miasto+
					 "' AND s.nazwa='"+specjalista+"';";
		//TODO
		printResult(executeQuery(query));
	}

	
	private void familyDoc() {
		String query="SELECT imie, nazwisko, nazwa, adres, nr_tel "+
					 "FROM osoby_info JOIN lekarze_placowki on lekarz_rodzinny=id_lekarz NATURAL JOIN placowki"+
					 "WHERE pesel='"+pesel+"' AND typ='przychodnia'";

		System.out.println("imie, nazwisko, nazwa, adres, nr_tel");
		printResult(executeQuery(query));
	}

	
	private void printResult(ResultSet result) {
		try {
			ResultSetMetaData metaData = result.getMetaData();
		    int cc = metaData.getColumnCount();
		    
		    while (result.next()) {
		    	for (int i = 1; i<=cc ; ++i){
		    		if (i!=1) System.out.print(", ");
		    		System.out.print(result.getString(i));
		    	}
		    	System.out.println();
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (SQLException ex) {
				System.out.println("cannot close resultset in recepts");
			}
		}
	}

	private void byebye() {
		// TODO Auto-generated method stub
		
	}
	
	public static void Main(String args[]){
		Patient michal = new Patient("64050701526", null);
		michal.perform();
	}
}
