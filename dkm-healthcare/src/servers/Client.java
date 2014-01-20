package servers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

import others.*;
//import protocols.P1protocol;

/**
 * Main class for Client.
 * @author dudu
 *
 */

public class Client {
	
	/**
	 * Method to get ResultSet from DBServers using P1 protocol.
	 * @param query
	 * @return ResultSet
	 */
	
	public static ResultSet getRSbyP1(String query) {
		BDServer bd = new BDServer();
		ResultSet rs = (ResultSet) bd.executeQuery(query);
		return rs;
	}
	
	/**
	 * Method to send modifying (insert, update, delete) query to DBServers 
	 * Uses P1protocol as well 
	 * @param query
	 */
	public static boolean updateBDbyP1(String query) {
		BDServer bd = new BDServer();
		bd.executeUpdate(query);
		return true;
	}
	
	/**
	 * It displays results stored in result. If variable continuee equals false, then it comes back to calling function. Otherwise it goes to menu, or exits.
	 * @param result
	 * @param continuee
	 * @param scaner
	 */
	public static void printResult(ResultSet result, boolean continuee, Scanner scan) {
		String cmd;
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
		    System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (SQLException ex) {
				System.out.println("cannot close resultset in recepts");
			}
		}
		if (!continuee) {
			System.out.println("aby powrocic do menu wpisz menu, aby zakonczyc wpisz exit");
			do {
				if ( (cmd=scan.nextLine()).equals("exit") ) System.exit(1);
				else if (cmd.equals("menu")) return;
				else continue;
			} while (true);
		}
	}

	
	
	/**
	 * method to change password.
	 * Static, because it is called from other static methods.
	 */
	private static void changePassword() {
		//TODO
	}
	
	/**
	 * main method. It runs when client starts application. 
	 * Here client signs in and then can work as patient, doctor or pharmacist (of course it depends on rights in data basis).
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		
		boolean isDoctor = false;
		boolean isPharm = false;
		
		
		System.out.println("Wpisz swoj pesel:");
		Scanner sc = new Scanner(System.in);
		String pesel = sc.next();
		System.out.println("Wprowadź hasło:");
		String password = sc.next();
		//log in with p1 protocol
		/*boolean success=false;
        while(success!=true){
            BDServer bd = new BDServer();
            try {
            bd.executeLogin();
            P1protocol.logIn(null, P1protocol.Site.User, null, pesel, password);
            } catch (Exception e) {
            	
            }
            System.out.println("Niepoprawne haslo, wprowadz jeszcze raz");
            password=sc.next();
        };*/
		
		String askIfDoctor = "select prawa from lekarze where id_lekarz = '" + pesel +"';";
		ResultSet rs = getRSbyP1(askIfDoctor);
		
		
		try {
			if (rs.next() && rs.getBoolean(1)) {
				
				isDoctor = true;
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String askIfPharmacist = "select aptekarz from osoby where pesel = '" + pesel + "';";
		rs = getRSbyP1(askIfPharmacist);
		
		try {
			if (rs.first() && rs.getBoolean(1)) {
				System.out.println("isPharm");
				isPharm = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		while(true) {
			System.out.println("\nWitamy w systemie dkm-healthcare! \n");
			System.out.println("Aby pracować w trybie pacjenta, wprowadź 1");
			if (isDoctor) System.out.println("Aby pracować w trybie lekarza, wprowadź 2");
			if (isPharm) System.out.println("Aby pracować w trybie aptekarza, wprowadź 3");
			System.out.println("Aby zmienić hasło, wprowadź 4");
			System.out.println("Aby zakończyć program, wprowadź 0");
			
			sc = new Scanner(System.in);
			int i = sc.nextInt();
			switch(i) {
			case 1:
				new Patient(pesel, password).perform();
				break;
			case 2:
				Doctor doctor = new Doctor(pesel, password);
				doctor.perform();
				break;
			case 3:
				Pharmacist pharm = new Pharmacist(pesel, password);
				pharm.perform();
				break;
			case 4:
				changePassword();
				break;
			case 0:
				System.out.println("Do zobaczenia!\n");
				sc.close();
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
				
		}
		
	}

}
