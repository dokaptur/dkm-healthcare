package others;

import java.util.Scanner;

public class Doctor {
	
	String pesel;
	
	public Doctor(String pesel) {
		this.pesel = pesel;
	}
	
	public void onePatient(Scanner sc) {
		
	}
	
	public void perform() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Aby pracować z jednym, konkretnym pacjentem, wprowadź 1 \n");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcję lekarza rodzinnego, wprowadź 2 \n");
			System.out.println("Aby wyświetlić wszystkich pacjentów, dla których pełnisz funkcę lekarza specjalisty, wprowadź 3 \n");
			System.out.println("Aby wyświetlić swoje uprawnienia, wprowadź 4 \n");
			System.out.println("Aby wyświetlić swoje specjalizacje, wprowadź 5 \n");
			System.out.println("Aby wyświetlić lub zmodyfikować swoje miejsca pracy, wprowadź 6 \n");
			System.out.println("Aby zakończyć, wprowadź 7 \n");
			System.out.println("Jeśli masz jakieś pytania/uwagi napisz do nas na adres dkm.dkmhealthcare@gmail.com \n");
			
			int i = sc.nextInt();
			switch(i) {
			case 1:
				onePatient(sc);
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
