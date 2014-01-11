package others;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import servers.Client;

/**
 * A class to work in doctor mode. It is created in Client class if client chose to work as doctor. 
 * @author dudu
 *
 */

public class Doctor {
	
	/**
	 * pesel of client
	 */
	String pesel;
	
	/**
	 * constructor
	 * @param pesel
	 */
	public Doctor(String pesel) {
		this.pesel = pesel;
	}
	
	/**
	 * method to work with one patient. In loop, client can choose what to do.
	 * Then sql queries are build and action is hold.
	 * @param sc
	 */
	
	private void onePatient(Scanner sc) {
		System.out.println("Wprowadź numer pesel pacjenta:");
		String patient = sc.next();
		boolean my = false;
		// check if it's my patient or not
		String check1 = "select count(*) from \"dkm-healthcare\".osoby_info where pesel = " + patient + " and lekarz_rodzinny = " + pesel + ";";
		String check2 = "select count(*) from \"dkm-healthcare\".pacjenci_specjalisci where pesel = " + patient + " and id_lekarz = " + pesel + ";";
		ResultSet rs1 = Client.getRSbyP1(check1);
		ResultSet rs2 = Client.getRSbyP1(check2);
		int n = 0;
		try {
			if (rs1.next()) {
				n += rs1.getInt(1);
			}
			if (rs2.next()) {
				n += rs2.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (n > 0) my = true;
		
		while(true) {
			System.out.println("Aby wyświetlić wżne informacje o pacjencie, wprowadź 1");
			System.out.println("Aby wyświetlić obecnie przyjmowane leki przez pacjenta, wprowadź 2");
			System.out.println("Aby wyświetlić alergie pacjenta, wprowadź 3");
			System.out.println("Aby wystawić receptę, wprowadź 4");
			System.out.println("Aby wystawić skierowanie, wprowadź 5");
			System.out.println("Aby zrealizować skierowanie, wprowadź 6");
			if (my) {
				System.out.println("Aby zobaczyć lub zmodyfikować historię choroby pacjenta, wprowadź 7");
			} else {
				System.out.println("Aby dostać adres email do lekarza rodzinnego tego pacjenta, wprowadź 7");
			}
			System.out.println("Aby zakończyć pracę z tym pacjentem, wprowadź 8");
			
			String query;
			n = sc.nextInt();
			switch(n) {
			case 1:
				query = "select info from \"dkm-healthcare\".osoby_info where pesel = " + patient + ";";
				getResult(query, 1, true);
				break;
			case 2:
				query = "select lek from \"dkm-healtcare\".osoby_leki where pesel = " + patient + ";";
				getResult(query, 1, true);
				break;
			case 3:
				query = "select alergia from \"dkm-healtcare\".osoby_alergie where pesel = " + patient + ";";
				getResult(query, 1, true);
				break;
			case 4:
				System.out.println("Wprowadź w jednej linii nazwę leku i dawkowanie:");
				String lek = sc.nextLine();
				query = "insert into \"dkm-healthcare\".recepty (pesel, id_lekarz, lek) values(" + patient + ", " + pesel + ", " + lek + ");";
				Client.getRSbyP1(query);
				break;
			case 5:
				addReferral(sc, patient);
				break;
			case 6:
				realizeReferral(sc, patient);
				break;
			case 7:
				if (my) {
					workWithHistory(sc, patient);
				} else {
					query = "select email from \"dkm-healthcare\".osoby where pesel = ("
							+ "select lekarz_rodzinny from \"dkm-healthcare\".osoby_info where pesel = " + patient + ");";
					getResult(query, 1, true);
				}
				break;
			case 8:
				System.out.println("Dziękujemy za pracę z pacjentam " + patient +"!");
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
				break;
			}
		}
	}
	
	/**
	 * short but very important method.
	 * Prints results of select queries onto StdIn
	 * @param rs 
	 * @param columns (how many columns our ResultSet has)
	 */
	
	public void printResult(ResultSet rs, int columns) {
		try {
			while (rs.next()) {
				for (int i=1; i<=columns; i++) {
					System.out.printf("%s ", rs.getString(i));
				}
				System.out.print("\n\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to get ResultSet using P1.
	 * @param query
	 * @param columns
	 * @param print
	 */
	
	private void getResult(String query, int columns, boolean print) {
		ResultSet rs = Client.getRSbyP1(query);
		if (print) {
			printResult(rs, columns);
		}
	}
	
	/**
	 * Method to download, modify and upload history
	 * @param sc
	 * @param patient
	 */
	
	private void workWithHistory(Scanner sc, String patient) {
		
	}
	
	/**
	 * Method to realize referral. 
	 * First, it checks if referral exist and is not realized yet.
	 * Then send query to update it.
	 * @param sc
	 * @param patient
	 */
	
	private void realizeReferral(Scanner sc, String patient) {
		System.out.println("Wprowadź numer skierowania:");
		String nr = sc.next();
		String query = "select * from skierowania where numer = " + nr + " and pesel = " + patient + ";";
		ResultSet rs = Client.getRSbyP1(query);
		try {
			if (rs.next()) {
				if (rs.getBoolean(5)) {
					System.out.println("To skierowanie zostało już wcześniej zrealizowane!");
				} else {
					query = "update \"dkm-healthcare\".skierowania set zrealizowany = true, zrealizowana_przez = " + pesel + 
							" where numer = " + nr + ";";
					Client.getRSbyP1(query);
				}
			} else {
				System.out.println("Następił błąd lub podane skierowanie nie istnieje!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add referral for a patient we work with.
	 * @param sc
	 * @param patient
	 */
	
	private void addReferral(Scanner sc, String patient) {
		System.out.println("Jeśli chcesz wyświetlić dostępne zabiegi, wprowadź 1");
		System.out.println("W przeciwnym wypadku wprowadź 2");
		int n = sc.nextInt();
		switch(n) {
		case 1:
			String query = "select * from \"dkm-healthcare\".zabiegi;";
			getResult(query, 2, true);
			break;
		case 2:
			break;
		default:
			System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");	
		}
		System.out.println("\nWprowadź numer identyfikacyjny zabiegu, na jaki chcesz wystawić skierowanie:");
		int id = sc.nextInt();
		String checkId = "select count(*) from \"dkm-healthcare\".zabiegi where id_zabieg = " + id + ";";
		ResultSet rs = Client.getRSbyP1(checkId);
		try {
			if (rs.next()) {
				if (rs.getInt(1) == 0) {
					System.out.println("Podany zabieg nie istnieje!");
				}
				else {
					String query = "insert into \"dkm-healthcare\".skierowania (pesel, id_lekarz, id_zabieg) values(" + patient + ", " + pesel + ", " + id + ");";
					Client.getRSbyP1(query);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method shows if doctor has rights to work as a doctor.
	 */
	
	private void rights() {
		String query = "select prawa from \"dkm-healthcare\".lekarze where id_lekarz = " + pesel + ";";
		ResultSet rs = Client.getRSbyP1(query);
		try {
			if (rs.next()) {
				if (rs.getBoolean(1)) {
					System.out.println("Masz prawo wykonywania zawodu.");
				} else {
					System.out.println("Niestety nie posiadasz aktualnie prawa wykonywania zawodu.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to show, add or remove work place
	 * @param sc
	 */
	
	private void workplaces(Scanner sc) {
		System.out.println("Aby wyświetlić swoje miejsca pracy, wprowadź 1");
		System.out.println("Aby usunąć miejsce pracy, wprowadź 2");
		System.out.println("Aby dodać miejsce pracy, wprowadź 3");
		
		ResultSet rs;
		String query = "select * from \"dkm-healthcare\".placowki where id_placowki in "
				+ "(select id_placowki from \"dkm-healthcare\".lekarze_placowki where id_lekarz = " + pesel + ";";
		int n = sc.nextInt();
		switch(n) {
		case 1:
			rs = Client.getRSbyP1(query);
			printResult(rs, 6);
			break;
		case 2:
			System.out.println("Pracujesz w następujących placówkach:\n");
			rs = Client.getRSbyP1(query);
			printResult(rs, 6);
			System.out.println("\nWprowadź numer identyfikacyjny placówki, którą chcesz usunąć ze swoich miejsc pracy:");
			String nr = sc.next();
			String delQuery = "delete from \"dkm-healthcare\".lekarze_placowki where id_placowki = " + nr + ";";
			Client.getRSbyP1(delQuery);
			break;
		case 3:
			System.out.println("Wprowadź numer identyfikacyjny placówki, którą chcesz dodać do swoich miejsc pracy:");
			String id = sc.next();
			String insQuery = "insert into \"dkm-healthcare\".lekarze_placowki values (" + pesel +", " + id + ");";
			Client.getRSbyP1(insQuery);
			break;
		}
	}
	
	/**
	 * Main "loop function" to work in doctor mode
	 */
	public void perform() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Aby pracować z jednym, konkretnym pacjentem, wprowadź 1");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcję lekarza rodzinnego, wprowadź 2");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcę lekarza specjalisty, wprowadź 3");
			System.out.println("Aby wyświetlić swoje uprawnienia, wprowadź 4");
			System.out.println("Aby wyświetlić swoje specjalizacje, wprowadź 5");
			System.out.println("Aby wyświetlić lub zmodyfikować swoje miejsca pracy, wprowadź 6");
			System.out.println("Aby zakończyć, wprowadź 7");
			System.out.println("Jeśli masz jakieś pytania/uwagi napisz do nas na adres dkm.dkmhealthcare@gmail.com");
			
			String query;
			int i = sc.nextInt();
			switch(i) {
			case 1:
				onePatient(sc);
				break;
			case 2:
				query = "select pesel, imie, nazwisko from \"dkm-healthcare\".osoby natural join \"dkm-healthcare\".osoby_info where lekarz_rodzinny = " +
						pesel + ";";
				getResult(query, 3, true);
				break;
			case 3:
				query = "select pesel, imie, nazwisko from \"dkm-healthcare\".osoby natural join \"dkm-healthcare\".pacjenci_specjalisci where id_lekarz = " +
						pesel + ";";
				getResult(query, 3, true);
				break;
			case 4:
				rights();
				break;
			case 5:
				query = "select nazwa from \"dkm-healthcare\".lekarze_specjalizacje natural join \"dkm-healthcare\".specjalizacje_where id_lekarz = " +
						pesel + ";";
				getResult(query, 1, true);
				break;
			case 6:
				workplaces(sc);
				break;
			case 7:
				System.out.println("Dziękujemy za pracę w trybie lekarza!\n");
				sc.close();
				return;
				
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
		}
	}
	
}
