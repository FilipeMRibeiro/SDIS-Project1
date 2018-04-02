package interfaces;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class ClientInterface {

	public static void main(String[] args) throws IOException {
		System.out.println("--CLIENT INTERFACE--");
		System.out.println("1. Backup Files");
		System.out.println("2. Restore Files");
		System.out.println("3. Delete Files");
		
		Scanner in = new Scanner(System.in);
		int option = in.nextInt();
		
		switch(option) {
		case 1: backupMenu();
				break;
		case 2: restoreMenu();
				break;
		case 3: deleteMenu();
				break;
		default: break;
		}
	}
	
	private static void deleteMenu() {
		boolean done = false;
		while(!done) {
			System.out.println();
			System.out.println("Path to file? (input 'exit' to exit)");
			Scanner in = new Scanner(System.in);
			String path = in.nextLine();
			
			if(path.compareTo("exit") == 0) {
				done = true;
			}
			else {
				File dir = new File(String.valueOf(path));
				if(!dir.exists()){
					System.out.println("This file does not exist");
				}
				else {
					//começaProtocoloDelete(); - alterar o nome - não sei como começar o protocolo
					done = true;
				}
			}
		}
	}

	private static void restoreMenu() {
		boolean done = false;
		while(!done) {
			System.out.println();
			System.out.println("Path to file? (input 'exit' to exit)");
			Scanner in = new Scanner(System.in);
			String path = in.nextLine();
			
			if(path.compareTo("exit") == 0) {
				done = true;
			}
			else {
				File dir = new File(String.valueOf(path));
				if(!dir.exists()){
					System.out.println("This file does not exist");
				}
				else {
					//começaProtocoloRestore(); - alterar o nome - não sei como começar o protocolo
					done = true;
				}
			}
		}
	}

	public static void backupMenu() {
		boolean done = false;
		while(!done) {
			System.out.println();
			System.out.println("Path to file? (input 'exit' to exit)");
			Scanner in = new Scanner(System.in);
			String path = in.nextLine();
			
			if(path.compareTo("exit") == 0) {
				done = true;
			}
			else {
				File dir = new File(String.valueOf(path));
				if(!dir.exists()){
					System.out.println("This file does not exist");
				}
				else {
					done = true;
				}
			}
		}
		done = false;
		while(!done) {
			System.out.println();
			System.out.println("Replication degree? (input 0 to exit)");
			Scanner in = new Scanner(System.in);
			int rep_degree = in.nextInt();
			
			if(rep_degree > 0 && rep_degree < 10) {
				//começaProtocoloBackup(); - alterar o nome - não sei como começar o protocolo

				done = true;
			}
			else {
				System.out.println("That replication degree is not acceptable");
			}
		}
	}
}
