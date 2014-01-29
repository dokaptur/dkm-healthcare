package servers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import others.*;
//import protocols.P1protocol;

/**
 * Main class for Client.
 * @author dudu
 *
 */

public class Client {
	
	Config config = new Config();
	String url = "jdbc:postgresql:dkm";
	String user = "dudu";
	String passwd = "ciap2000";
	public Connection conn; 
	
	public Client() {
		try {
		    Class.forName("org.postgresql.Driver");
		    
		    conn = DriverManager.getConnection(url, user, passwd);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to get ResultSet from DBServers using P1 protocol.
	 * @param query
	 * @return ResultSet
	 */
	
	public ResultSet getRSbyP1(String query) {
		ResultSet result = null;
		try {
		    Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		    stat.execute("SET search_path TO 'dkm-healthcare'");
		    result = stat.executeQuery(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		} 
		return result;
	}
	
	/**
	 * Method to send modifying (insert, update, delete) query to DBServers 
	 * Uses P1protocol as well 
	 * @param query
	 */
	public boolean updateBDbyP1(String query) {
		try {
		    Statement stat = conn.createStatement();
		    stat.execute("SET search_path TO 'dkm-healthcare'");
		    stat.executeUpdate(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		}
		return true;
	}
	
	/**
	 * It displays results stored in result. If variable continuee equals false, then it comes back to calling function. Otherwise it goes to menu, or exits.
	 * @param result
	 * @param continuee
	 * @param scaner
	 */
	public static void printResult(ResultSet result, boolean continuee, Scanner scan) {
		int cmd;
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
			System.out.println("aby powrocic do menu wciśnij 1, aby zakonczyc program wciśnij 0");
			do {
				if ((cmd=scan.nextInt()) == 0) System.exit(1);
				else if (cmd == 1) return;
				else continue;
			} while (true);
		}
	}
	
	private String md5Hash(String s) {
		String result = null;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(s.getBytes());
			byte[] md5sum = md5.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			result = bigInt.toString(16);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * method to change password.
	 */
	public void changePassword(String pesel, Scanner sc) {
		do {
			String h1, h2;
			System.out.println("Wprowadź nowe hasło:");
			h1 = sc.next();
			System.out.println("Powtórz nowe hasło:");
			h2 = sc.next();
			if (!h1.equals(h2)) {
				System.out.println("Wprowadzone hasła są inne! Spróbuj jeszcze raz!");
			} else {
				this.updateBDbyP1("update osoby set haslo = '" + md5Hash(h1) + "' where pesel = '" + pesel + "';");
				System.out.println("Hasło zostało zmienione.");
				break;
			}
		} while (true);
	}
	
	public boolean checkpasswd(String pesel, String password) {
		
		String passwdmd5;
		String passwdok;
		try {
			passwdmd5 = md5Hash(password);
			//System.out.println(passwdmd5);
			
			ResultSet rs = this.getRSbyP1("select haslo from osoby where pesel = '" + pesel + "';");
			rs.next();
			passwdok = rs.getString(1);
			//System.out.println(passwdok);
			
			if (passwdmd5.equals(passwdok)) return true;
			else {
				System.out.println("\nNieprawidłowy pesel lub hasło! Spróbuj jeszcze raz!\n");
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * main method. It runs when client starts application. 
	 * Here client signs in and then can work as patient, doctor or pharmacist (of course it depends on rights in data basis).
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		Client client = new Client();
		boolean isDoctor = false;
		boolean isPharm = false;
		Scanner sc = new Scanner(System.in);
		String pesel;
		
		do {
			
			System.out.println("Wpisz swoj pesel:");
			pesel = sc.next();
			System.out.println("Wprowadź hasło:");
			String password = sc.next();
			
			if (client.checkpasswd(pesel, password)) break;
		} while (true);
		
		
		
		String askIfDoctor = "select prawa from lekarze where id_lekarz = '" + pesel +"';";
		ResultSet rs = client.getRSbyP1(askIfDoctor);
		try {
			if (rs.next() && rs.getBoolean(1)) {
				isDoctor = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String askIfPharmacist = "select aptekarz from osoby where pesel = '" + pesel + "';";
		rs = client.getRSbyP1(askIfPharmacist);
		
		try {
			if (rs.first() && rs.getBoolean(1)) {
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
				new Patient(pesel, client).perform();
				break;
			case 2:
				Doctor doctor = new Doctor(pesel, client);
				doctor.perform();
				break;
			case 3:
				Pharmacist pharm = new Pharmacist(pesel, client);
				pharm.perform();
				break;
			case 4:
				client.changePassword(pesel, sc);
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
