package others;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
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
	Client client;
	Scanner sc;
	
	/**
	 * constructor
	 * @param pesel
	 * @param client
	 */
	public Doctor(String pesel, Client client) {
		this.pesel = pesel;
		this.client = client;
		sc = new Scanner(System.in);
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
		String check1 = "select count(*) from osoby where pesel = '" + patient + "' and lekarz_rodzinny = '" + pesel + "';";
		String check2 = "select count(*) from pacjenci_specjalisci where pesel = '" + patient + "' and id_lekarz = '" + pesel + "';";
		ResultSet rs1 = client.getRSbyP1(check1);
		ResultSet rs2 = client.getRSbyP1(check2);
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
			System.out.println("Aby wyświetlić lub zmodyfikować ważne informacje o pacjencie, wprowadź 1");
			System.out.println("Aby wyświetlić  lub zmodyfikować leki przyjmowane przez pacjenta, wprowadź 2");
			System.out.println("Aby wyświetlić lub zmodyfikować alergie pacjenta, wprowadź 3");
			System.out.println("Aby wystawić receptę, wprowadź 4");
			System.out.println("Aby wystawić skierowanie, wprowadź 5");
			System.out.println("Aby zrealizować skierowanie, wprowadź 6");
			if (my) {
				System.out.println("Aby ściągnąć lub zmodyfikować historię choroby pacjenta, wprowadź 7");
			} else {
				System.out.println("Aby dostać adres email do lekarza rodzinnego tego pacjenta, wprowadź 7");
			}
			System.out.println("Aby zakończyć pracę z tym pacjentem, wprowadź 0");
			System.out.println();
			
			String query;
			n = sc.nextInt();
			switch(n) {
			case 1:
				patientInfo(patient);
				break;
			case 2:
				patientPharm(patient);
				break;
			case 3:
				patientAllergy(patient);
				break;
			case 4:
				System.out.println("Wprowadź w jednej linii nazwę leku i dawkowanie:");
				sc.nextLine();
				String lek =  sc.nextLine();
				query = "insert into recepty (pesel, id_lekarz, lek) values('" + patient + "', '" + pesel + "', '" + lek + "');";
				if (client.updateBDbyP1(query)) {
					System.out.println("\nRecepta została dodana.\n");
				} else {
					System.out.println("Nastąpił błąd!");
				}
				break;
			case 5:
				addReferral(patient);
				break;
			case 6:
				realizeReferral(patient);
				break;
			case 7:
				if (my) {
					workWithHistory(patient);
				} else {
					query = "select email from osoby where pesel = ("
							+ "select lekarz_rodzinny from osoby_info where pesel = '" + patient + "');";
					Client.printResult(client.getRSbyP1(query), false, sc);
				}
				break;
			case 0:
				System.out.println("Dziękujemy za pracę z pacjentam " + patient +"!");
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
				break;
			}
		}
	}
	/*
	public static void printResult(ResultSet rs, int columns) {
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
	} */
	
	/**
	 * Method to show or modify important info about patient
	 * @param sc
	 * @param patient
	 */
	
	private void patientInfo(String patient) {
		System.out.println("Aby wyświetlić informacje o pacjencie, wprowadź 1");
		System.out.println("Aby dodać nową informację o pacjencie, wprowadź 2");
		System.out.println("Aby zdezaktualizować informacje o pacjencie, wprowadź 3\n");
		int n = sc.nextInt();
		String query = "select id, info, aktualne from osoby_info where pesel = '" + patient + "';";
		ResultSet rs = client.getRSbyP1(query);
		
		switch (n) {
		case 1:
			System.out.println("\nID, info, aktualne\n");
			Client.printResult(rs, false, sc);
			break;
		case 2 :
			System.out.println("Wprowadź w jednej lini informację, którą chcesz dodać:");
			sc.nextLine();
			String info = sc.nextLine();
			if (client.updateBDbyP1("insert into osoby_info(pesel, info, aktualne) values('" + patient +"','"+ info +"', true);")) {
				System.out.println("\nInformacje zostały dodane.\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		
		case 3 :
			System.out.println("Wprowadź numer ID informacji, którą chcesz zdezaktualizować:");
			int id = sc.nextInt();
			if (client.updateBDbyP1("update osoby_info set aktualne = false where pesel = '" + patient + "' and id = " + id +";")) {
				System.out.println("\nInformacje zostały zmodyfikowane.\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		}
	}
	
	/**
	 * Method to show or modify patient's drugs
	 * @param sc
	 * @param patient
	 */
	
	private void patientPharm(String patient) {
		String query;
		System.out.println("Aby wyświetlić wszystkie przyjmowane leki, wprowadź 1");
		System.out.println("Aby wyświetlić obecnie przyjmowane leki, wprowadź 2");
		System.out.println("Aby dodać lek, wprowadź 3");
		System.out.println("Aby dodać datę zakończenia przyjmowania leku, wprowadź 4\n");
		int n = sc.nextInt();
		switch (n) {
		case 1 :
			query = "select id, lek, od, \"do\" from osoby_leki where pesel = '" + patient + "';";
			System.out.println("\nID, lek, od, do\n");
			Client.printResult(client.getRSbyP1(query), false, sc);
			break;
		case 2 :
			query = "select id, lek, od from osoby_leki where pesel = '" + patient +"' and \"do\" IS NULL;";
			System.out.println("\nID, lek, od\n");
			Client.printResult(client.getRSbyP1(query), false, sc);
			break;
		case 3:
			System.out.println("Wprowadź w jednej lini lek, który chcesz dodać");
			sc.nextLine();
			String lek = sc.nextLine();
			if (client.updateBDbyP1("insert into osoby_leki(pesel, lek) values('" + patient + "', '" + lek + "');")) {
				System.out.println("\nLek został dodany.\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		case 4:
			System.out.println("Wprowadź numer ID leku");
			int id = sc.nextInt();
			System.out.println("Wprowadź datę \"do\" w formacie YYYY-MM-DD:");
			String date = sc.next();
			if (client.updateBDbyP1("update osoby_leki set \"do\" = '" + date + "' where id = " + id + " and pesel = '" + patient + "';")) {
				System.out.println("\nData została zmodyfikowana\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		}
	}
	
	/**
	 * Method to show or modify patient's allergies
	 * @param sc
	 * @param patient
	 */
	
	private void patientAllergy(String patient) {
		String query ;
		
		System.out.println("Aby wyświetlić alergie, wprowadź 1");
		System.out.println("Aby dodać alergie, wprowadź 2");
		System.out.println("Aby zdezaktualizować alergię, wprowadź 3\n");
		int n = sc.nextInt();
		switch (n) {
		case 1 :
			query = "select id, alergia, aktualne from osoby_alergie where pesel = '" + patient + "';";
			System.out.println("\nID, alergia, aktualne");
			Client.printResult(client.getRSbyP1(query), false, sc);
			break;
		case 2 :
			System.out.println("Wprowadź w jednej lini alergię, którą chcesz dodać:");
			sc.nextLine();
			String info = sc.nextLine();
			if (client.updateBDbyP1("insert into osoby_alergie(pesel, alergia, aktualne) values('" + patient +"','"+ info +"', true);")) {
				System.out.println("\nInformacje zostały dodane.\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		
		case 3 :
			System.out.println("Wprowadź numer ID alergii, którą chcesz zdezaktualizować:");
			int id = sc.nextInt();
			if (client.updateBDbyP1("update osoby_alergie set aktualne = false where pesel = '" + patient + "' and id = " + id +";")) {
				System.out.println("\nInformacje zostały zmodyfikowane.\n");
			} else {
				System.out.println("\nNastąpił bład!\n");
			}
			break;
		}
	}
	
	
	/**
	 * Method to download, modify and upload history
	 * @param sc
	 * @param patient
	 */
	
	private void workWithHistory(String patient) {
		System.out.println("Aby ściągnąć historię choroby pacjenta, wprowadź 1");
		System.out.println("Aby dodać pliki do historii choroby pacjenta, wprowadź 2");
		System.out.println("Jeśli chcesz wrócić, wprowadź 0\n");
		int n = sc.nextInt();
		String path;
		
		switch (n) {
		case 1:
			System.out.println("Wprowadź ścieżkę do folderu, do którego chcesz ściągnąć folder z historią choroby:");
			path = sc.next(".*[a-zA-Z1-9\\/]*");
			String query = "select * from osoby_historia where pesel = '" + patient + "';";
			ResultSet rs = client.getRSbyP1(query);
			try {
				while (rs.next()) {
					String filename = rs.getString("nazwa");
					FileOutputStream fos = new FileOutputStream(path + filename);
					fos.write(rs.getBytes("plik"));
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\nHistoria choroby pacjenta dostępna pod adresem " + path + "\n");
			break;
		case 2 :
			System.out.println("Wprowadź ścieżkę do pliku, który chcesz dodać do historii choroby pacjenta:");
			path = sc.next(".*[a-zA-Z1-9\\/]*");
			File file = new File(path);
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				PreparedStatement ps = client.conn.prepareStatement("INSERT INTO osoby_historia(pesel, plik, nazwa) VALUES (?, ?, ?)");
				ps.setString(1, patient);
				ps.setString(3, file.getName());
				ps.setBinaryStream(2, fis, (int)file.length());
				ps.executeUpdate();
				ps.close();
				fis.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			String queryy = "update osoby set historia_modyfikacja = current_date where pesel = '" + patient + "';";
			client.updateBDbyP1(queryy);
			System.out.println("\nPlik został załadowany\n");
			break;
		case 0 :
			return;
		}
	}
	
	/**
	 * Method to realize referral. 
	 * First, it checks if referral exist and is not realized yet.
	 * Then send query to update it.
	 * @param sc
	 * @param patient
	 */
	
	private void realizeReferral(String patient) {
		System.out.println("Wprowadź numer skierowania:");
		String nr = sc.next();
		String query = "select * from skierowania where numer = " + nr + " and pesel = '" + patient + "';";
		ResultSet rs = client.getRSbyP1(query);
		try {
			if (rs.next()) {
				if (rs.getBoolean(5)) {
					System.out.println("To skierowanie zostało już wcześniej zrealizowane!\n");
				} else {
					query = "update skierowania set zrealizowany = true where numer = " + nr + ";";
					client.updateBDbyP1(query);;
				}
			} else {
				System.out.println("Następił błąd lub podane skierowanie nie istnieje!\n");
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
	
	private void addReferral(String patient) {
		System.out.println("Jeśli chcesz wyświetlić dostępne zabiegi, wprowadź 1");
		System.out.println("W przeciwnym wypadku wprowadź 2");
		int n = sc.nextInt();
		switch(n) {
		case 1:
			String query = "select * from zabiegi;";
			System.out.println("\nid, nazwa\n");
			Client.printResult(client.getRSbyP1(query), true, sc);
			break;
		case 2:
			break;
		default:
			System.out.println("\nNastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.\n");	
		}
		System.out.println("\nWprowadź numer identyfikacyjny zabiegu, na jaki chcesz wystawić skierowanie:");
		int id = sc.nextInt();
		String checkId = "select count(*) from zabiegi where id_zabieg = " + id + ";";
		ResultSet rs = client.getRSbyP1(checkId);
		try {
			if (rs.next()) {
				if (rs.getInt(1) == 0) {
					System.out.println("\nPodany zabieg nie istnieje!\n");
				}
				else {
					String query = "insert into skierowania (pesel, id_lekarz, id_zabieg) values('" + patient + "', '" + pesel + "', " + id + ");";
					if (client.updateBDbyP1(query)) {
						System.out.println("\nSkierowanie zostało dodane.\n");
					} else {
						System.out.println("\nNastąpił błąd!\n");
					}
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
		String query = "select prawa from lekarze where id_lekarz = '" + pesel + "';";
		ResultSet rs = client.getRSbyP1(query);
		try {
			if (rs.next()) {
				if (rs.getBoolean(1)) {
					System.out.println("\nMasz prawo wykonywania zawodu.\n");
				} else {
					System.out.println("\nNiestety nie posiadasz aktualnie prawa wykonywania zawodu.\n");
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
		String query = "select * from placowki where id_placowka in "
				+ "(select id_placowka from lekarze_placowki where id_lekarz = '" + pesel + "');";
		int n = sc.nextInt();
		switch(n) {
		case 1:
			rs = client.getRSbyP1(query);
			System.out.println("\nid_placowki, typ, nazwa, adres, miasto, numer telefonu\n");
			Client.printResult(rs, false, sc);
			break;
		case 2:
			System.out.println("Pracujesz w następujących placówkach:\n");
			rs = client.getRSbyP1(query);
			System.out.println("\nid_placowki, typ, nazwa, adres, miasto, numer telefonu\n");
			Client.printResult(rs, true, sc);
			System.out.println("\nWprowadź numer identyfikacyjny placówki, którą chcesz usunąć ze swoich miejsc pracy:");
			String nr = sc.next();
			String delQuery = "delete from lekarze_placowki where id_placowka = " + nr + ";";
			if (client.updateBDbyP1(delQuery)) {
				System.out.println("\nPlacówka została usunięta z Twoich miejsc pracy.\n");
			} else {
				System.out.println("\nNastąpił błąd!\n");
			}
			break;
		case 3:
			System.out.println("Wprowadź numer identyfikacyjny placówki, którą chcesz dodać do swoich miejsc pracy:");
			String id = sc.next();
			String insQuery = "insert into lekarze_placowki values ('" + pesel +"', " + id + ");";
			if (client.updateBDbyP1(insQuery)) {
				System.out.println("\nPlacówka została dodana do Twoich miejsc pracy.\n");
			} else {
				System.out.println("\nNastąpił błąd!\n");
			}
			break;
		}
	}
	
	/**
	 * Main "loop function" to work in doctor mode
	 */
	public void perform() {
		
		while (true) {
			System.out.println("Aby pracować z jednym, konkretnym pacjentem, wprowadź 1");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcję lekarza rodzinnego, wprowadź 2");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcę lekarza specjalisty, wprowadź 3");
			System.out.println("Aby wyświetlić swoje uprawnienia, wprowadź 4");
			System.out.println("Aby wyświetlić swoje specjalizacje, wprowadź 5");
			System.out.println("Aby wyświetlić lub zmodyfikować swoje miejsca pracy, wprowadź 6");
			System.out.println("Aby zakończyć pracę w trybie lekarza, wprowadź 0");
			System.out.println("Jeśli masz jakieś pytania/uwagi napisz do nas na adres dkm.dkmhealthcare@gmail.com");
			
			String query;
			int i = sc.nextInt();
			switch(i) {
			case 1:
				onePatient(sc);
				break;
			case 2:
				query = "select pesel, imie, nazwisko from osoby  where lekarz_rodzinny = '" + pesel + "';";
				System.out.println("\nPesel, Imię, Nazwisko\n");
				Client.printResult(client.getRSbyP1(query), false, sc);
				break;
			case 3:
				query = "select pesel, imie, nazwisko from osoby natural join pacjenci_specjalisci where id_lekarz = '" +
						pesel + "';";
				System.out.println("\nPesel, Imię, Nazwisko\n");
				Client.printResult(client.getRSbyP1(query), false, sc);
				break;
			case 4:
				rights();
				break;
			case 5:
				query = "select nazwa from lekarze_specjalizacje natural join specjalizacje where id_lekarz = '" + pesel + "';";
				System.out.println("\nSpecjalizacje:\n");
				Client.printResult(client.getRSbyP1(query), false, sc);
				break;
			case 6:
				workplaces(sc);
				break;
			case 0:
				System.out.println("Dziękujemy za pracę w trybie lekarza!\n");
				//sc.close();
				return;
				
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
		}
	}
	
}
