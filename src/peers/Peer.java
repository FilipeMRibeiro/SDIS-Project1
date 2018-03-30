package peers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class Peer {
	
	private static int id;
	private static String directory; //directory for this peer's files
	private static int space; //amount of space available to this peer
	
	private static int srvc_port;
	private static String mcast_addr;
	private static int mcast_port;
	private static String service_address = "127.0.0.1";
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.out.println("Usage: Peer <id> <space> <mcast_addr> <mcast_port>");
			return;
		}
		id = Integer.parseInt(args[0]);
		space = Integer.parseInt(args[1]);
		mcast_addr = args[2];
		mcast_port = Integer.parseInt(args[3]);
		directory = "/home/filipe/Downloads/peer" + id + "/";
		
		//Open MulticastSocket
		MulticastSocket multicast_socket = new MulticastSocket(mcast_port);
		InetAddress multicast_address = InetAddress.getByName(mcast_addr);
		multicast_socket.joinGroup(multicast_address);
		System.out.println("Listening on multicast address " + mcast_addr);
		
		boolean done = false;
		while(!done) {
			
			//Receives file number
			byte[] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			multicast_socket.receive(packet);
			String filenumber = new String(packet.getData(), 0, packet.getLength());
			
			
			// Receives filename
			buffer = new byte[256];
			packet = new DatagramPacket(buffer, buffer.length);
			multicast_socket.receive(packet);
			String filename = new String(packet.getData(), 0, packet.getLength());
			
			//Receives file content
			buffer = new byte[64000];
			packet = new DatagramPacket(buffer, buffer.length);
			multicast_socket.receive(packet);
			String content = new String(packet.getData(), 0, packet.getLength());

			//Opens filestream to output the copied data to a new file of size = chunkSize
			File dir = new File(String.valueOf(directory));

			if(!dir.exists()){
				dir.mkdir();
			}
			
			File file = new File(directory + filenumber + "_" + filename);
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(content.getBytes()); // bytesRead is used to make sure that all chunks only get relevant data. This avoids the last chunk having jitter at the end of it caused by data from the previous chunk	
			fileOutput.close();
		}
	}
}
