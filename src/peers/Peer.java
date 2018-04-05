package peers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import listeners.*;
import protocols.Backup;
import protocols.Delete;
import protocols.Restore;


public class Peer {
	
	public static String id;
	public static int port_number;
	
	public static void main(String[] args) throws IOException {
		
		if (args == null) {
			System.out.println("Invalid arguments - Expected: [<Id> <port_number> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber>");
			return;
		}
		if (args.length != 5) {
			System.out.println("Invalid arguments - Expected: [<Id> <port_number> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber>");
			return;
		}
		
		id = args[0];
		port_number = Integer.parseInt(args[1]);
		
		StaticVariables.mcAddress = InetAddress.getByName(args[2].split(":")[0]);
		StaticVariables.mcPort = Integer.parseInt(args[2].split(":")[1]);
		
		StaticVariables.mdbAddress = InetAddress.getByName(args[3].split(":")[0]);
		StaticVariables.mdbPort = Integer.parseInt(args[3].split(":")[1]);
		
		StaticVariables.mdrAddress = InetAddress.getByName(args[4].split(":")[0]);
		StaticVariables.mdrPort = Integer.parseInt(args[4].split(":")[1]);
		
		joinMulticastGroups();
		InitializeListeners();

		DatagramSocket socket = new DatagramSocket(port_number);
		
		while(true) {
			byte[] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);

			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Received request: " + received);
			
			String protocol = received.split(" ")[0];
			String path = received.split(" ")[1];
			int index = path.lastIndexOf("/");
			String fileName = path.substring(index + 1);
			index = path.lastIndexOf(fileName);
			String directory = path.substring(0, index);
			System.out.println(directory);
			
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			String response;
			
			switch(protocol) {
			case "BACKUP":
				int rep_degree = Integer.parseInt(received.split(" ")[2]);
				Backup.backupFile(directory, fileName, rep_degree);
				response = "BACKUP completed";
				break;
			case "DELETE":
				Delete.initiateProtocol(fileName, directory);
				response = "DELETE completed";
				break;
			case "RESTORE":
				Restore.initiateProtocol(fileName, directory);
				response = "RESTORE completed";
				break;
			case "RECLAIM":
				response = "This protocol is not implemented yet.";
				break;
			default:
				response = "Could not detect protocol.";
				break;
			}
			
			buffer = response.getBytes();
			packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
		}		
	}
	
	
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