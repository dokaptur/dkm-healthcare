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
		ResultSet rs = null; //get by P1
		return rs;
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
		
		
		System.out.println("Type your pesel:\n");
		Scanner sc = new Scanner(System.in);
		String pesel = sc.next();
		System.out.println("Type your password: \n");
		String password = sc.next();
		//log in with p1 protocol
		
		String askIfDoctor = "select count(*) from \"dkm-healthcare\".lekarze where id_lekarz = " + pesel + ";";
		ResultSet rs = getRSbyP1(askIfDoctor);
		
		try {
			if (rs.next()) {
				if(rs.getInt(1) == 1) {
					isDoctor = true;
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String askIfPharmacist = "select aptekarz from \"dkm-healthcare\".osoby where pesel = " + pesel + ";";
		rs = getRSbyP1(askIfPharmacist);
		
		try {
			if (rs.next()) {
				if (rs.getBoolean(1)) {
					isPharm = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		while(true) {
			System.out.println("Witamy w systemie dkm-healthcare! \n");
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
