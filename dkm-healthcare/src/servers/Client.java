package servers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import others.*;

public class Client {
	
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
		ResultSet rs = null; // ask with p1
		
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
		// get answer to rs - P1 protocol
		
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
			System.out.println("Witamy w systemie dkm-healthcare!\n");
			System.out.println("Aby pracować w trybie pacjenta, wprowadź 1 \n");
			if (isDoctor) System.out.println("Aby pracować w trybie lekarza, wprowadź 2 \n");
			if (isPharm) System.out.println("Aby pracować w trybie aptekarza, wprowadź 3 \n");
			System.out.println("Aby zakończyć program, wprowadź 4 \n");
			
			int i = sc.nextInt();
			switch(i) {
			case 1:
				// patient class!!
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
				System.out.println("Do zobaczenia!\n");
				sc.close();
				return;
			default:
				System.out.println("Nastąpił błąd lub została wprowadzona nieprawidłowa wartość. Spróbuj jeszcze raz.");
			}
				
		}
		
	}

}
