package peers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import interfaces.ClientInterface;
import listeners.*;
import protocols.Backup;


public class Peer {
	
	public static String id;
	

	
	
	
	
	public static void main(String[] args) throws IOException {
		
		if (args == null) {
			System.out.println("Invalid arguments - Expected: [<Id> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber> <IS_INITIATOR>");
			return;
		}
		if (args.length != 5) {
			System.out.println("Invalid arguments - Expected: [<Id> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber> <IS_INITIATOR>");
			return;
		}
		
		id = args[0];
		
		StaticVariables.mcAddress = InetAddress.getByName(args[1].split(":")[0]);
		StaticVariables.mcPort = Integer.parseInt(args[1].split(":")[1]);

		
		
		StaticVariables.mdbAddress = InetAddress.getByName(args[2].split(":")[0]);
		StaticVariables.mdbPort = Integer.parseInt(args[2].split(":")[1]);
		
		StaticVariables.mdrAddress = InetAddress.getByName(args[3].split(":")[0]);
		StaticVariables.mdrPort = Integer.parseInt(args[3].split(":")[1]);
		
		joinMulticastGroups();
		InitializeListeners();
		
		
		
		//FOR NOW
		//HARDCODED CALL TO A PROTOCOL

		if(args[4].equals("1"))
			Backup.test();
		
	}
	

	
	//TODO
	//RECEIVE REQUESTS FROM CLIENT
	
	
	public static void sendStoredMessage(String fileId, String chunkNo) throws InterruptedException, IOException {
		String message = "STORED" + " " + StaticVariables.version + " " + Peer.id + " " + fileId + " " + chunkNo + " " + StaticVariables.CRLF2;
		DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), StaticVariables.mcAddress, StaticVariables.mcPort);

		//Random Delay from 0 to 400ms to avoid congestion
		Random rand = new Random();
		int delay = rand.nextInt(400) + 1;
		Thread.sleep(delay);
		
		StaticVariables.mcSocket.send(messagePacket);
		
	}
	
	
	
	private static void joinMulticastGroups() throws IOException {
		StaticVariables.mcSocket = new MulticastSocket(StaticVariables.mcPort);
		StaticVariables.mcSocket.joinGroup(StaticVariables.mcAddress);
		StaticVariables.mcSocket.setTimeToLive(1);
		

		
		
		StaticVariables.mdbSocket = new MulticastSocket(StaticVariables.mdbPort);
		StaticVariables.mdbSocket.joinGroup(StaticVariables.mdbAddress);
		StaticVariables.mdbSocket.setTimeToLive(1);
		
		StaticVariables.mdrSocket = new MulticastSocket(StaticVariables.mdrPort);
		StaticVariables.mdrSocket.joinGroup(StaticVariables.mdrAddress);
		StaticVariables.mdrSocket.setTimeToLive(1);
	}
	
	private static void InitializeListeners(){
		StaticVariables.mcListener = new MCListener();
			new Thread(StaticVariables.mcListener).start();
		StaticVariables.mdrListener = new MDRListener();
			new Thread(StaticVariables.mdrListener).start();
		StaticVariables.mdbListener = new MDBListener();
			new Thread(StaticVariables.mdbListener).start();
	}
	
}