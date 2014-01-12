package servers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


import others.*;

/**
 * Main class for Client.
 * @author dudu
 *
 */

public class Client {
	
	/**
	 * Method to get ResultSet from DBServers using P1 protocol.
	 * @param query
	 * @return
	 */
	
	public static ResultSet getRSbyP1(String query) {
		BDServer bd = new BDServer();
		ResultSet rs = (ResultSet) bd.executeQuery(query);
		return rs;
	}
	
	public static void updateBDbyP1(String query) {
		BDServer bd = new BDServer();
		bd.executeUpdate(query);
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
		
		String askIfDoctor = "select * from lekarze where id_lekarz = '" + pesel +"';";
		ResultSet rs = getRSbyP1(askIfDoctor);
		
		
		try {
			if (rs.first()) {
				
				System.out.println("isDoctor");
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
			System.out.println("Aby zakończyć program, wprowadź 5");
			
			int i = sc.nextInt();
			switch(i) {
			case 1:
				new Patient(pesel).perform();
				break;
			case 2:
				Doctor doctor = new Doctor(pesel);
				doctor.perform();
				break;
			case 3:
				Pharmacist pharm = new Pharmacist(pesel);
				pharm.perform();
				break;
			case 4:
				changePassword();
				break;
			case 5:
				System.out.println("Do zobaczenia!\n");
				sc.close();
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
				
		}
		
	}

}
