package others;

import java.util.Scanner;

import servers.Client;

public class Pharmacist {
	
	String pesel;
	
	public Pharmacist (String pesel) {
		this.pesel = pesel;
	}
	
	private void realizePrecsription(Scanner sc) {
		System.out.println("Wprowadź numer recepty:\n");
		String nr = sc.next();
		String query = "update \"dkm-healthcare\".recepty set zrealizowana = true, zrealizowana_przez = " + pesel + 
				" where numer = " + nr + ";";
		Client.getRSbyP1(query);
		
	}
	
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
