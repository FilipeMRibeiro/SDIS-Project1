package peers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import protocols.Backup;


public class Peer {
	
	private static int id;
	private static String directory; //directory for this peer's files
	private static int space; //amount of space available to this peer
	
	private static String mcast_addr;
	private static int mcast_port = 8000;
	private static String service_address = "127.0.0.1";
	
	private static int initiator;
	private static Backup backup;
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.out.println("Usage: Peer <id> <space> <mcast_addr> <mcast_port>");
			return;
		}
		id = Integer.parseInt(args[0]);
		space = Integer.parseInt(args[1]);
		mcast_addr = args[2];
		initiator = Integer.parseInt(args[3]);
		directory = "/home/filipe/Downloads/peer" + id + "/";
		
		
		//Open MulticastSocket
		MulticastSocket multicast_socket = new MulticastSocket(mcast_port);
		InetAddress multicast_address = InetAddress.getByName(mcast_addr);
		multicast_socket.joinGroup(multicast_address);
		
		if(initiator == 1) {
			String message = "hello";
			String request = "PUTCHUNK <Version> " + id + " test <ChunkNo> <ReplicationDeg> " + message;
			DatagramPacket request_packet = new DatagramPacket(request.getBytes(), request.getBytes().length, multicast_address, mcast_port);
			multicast_socket.send(request_packet);
		}
		
		boolean done = false;
		while(!done) {
			
			byte[] buffer = new byte[65000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			multicast_socket.receive(packet);
			String received = new String(packet.getData());
			String[] parsed = received.split(" ");
			String protocol = parsed[0];
			String version = parsed[1];
			int sender_id = Integer.parseInt(parsed[2]);
			
			if(sender_id != id) {
				String file_id = parsed[3];
				//int chunk_no = Integer.parseInt(parsed[4]);
				//int replication_deg = Integer.parseInt(parsed[5]);
				System.out.println("received: " + received);
				if(protocol.compareTo("PUTCHUNK") == 0) {
					String body = parsed[6];
					backupChunk(file_id, body);
					buffer = new byte[256];
					String response = "STORED " + version + " " + id + " " + file_id + " <ChunkNo> " + body;
					//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					packet = new DatagramPacket(response.getBytes(), response.getBytes().length, multicast_address, mcast_port);
					multicast_socket.send(packet);
				}
				else if(protocol.compareTo("GETCHUNK") == 0) {
					//restore();
					
					String response = "CHUNK " + version + " " + id + " " + file_id + " <ChunkNo> ";
					//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					packet = new DatagramPacket(response.getBytes(), response.getBytes().length, multicast_address, mcast_port);
					multicast_socket.send(packet);
				}
				else if(protocol.compareTo("DELETE") == 0) {
					//delete();
					
					String response = "DELETED " + version + " " + id + " " + file_id;
					//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					packet = new DatagramPacket(response.getBytes(), response.getBytes().length, multicast_address, mcast_port);
					multicast_socket.send(packet);
				}
			}
			else {
				buffer = new byte[256];
				packet = new DatagramPacket(buffer, buffer.length);
				multicast_socket.receive(packet);
				String answer = new String(packet.getData());
				System.out.println("received: " + answer);
			}
			
			/*
			//Receives file number
			buffer = new byte[256];
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
			
			File file = new File(directory + filename);
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(content.getBytes()); // bytesRead is used to make sure that all chunks only get relevant data. This avoids the last chunk having jitter at the end of it caused by data from the previous chunk	
			fileOutput.close();
			*/
		}
	}
	
	public static void backupChunk(String file_id, String body) throws IOException {
		
		//Opens filestream to output the copied data to a new file of size = chunkSize
		File dir = new File(String.valueOf(directory));
		if(!dir.exists()){
			dir.mkdir();
		}

		File file = new File(directory + file_id);
		FileOutputStream fileOutput = new FileOutputStream(file);
		fileOutput.write(body.getBytes(), 0, body.getBytes().length); // bytesRead is used to make sure that all chunks only get relevant data. This avoids the last chunk having jitter at the end of it caused by data from the previous chunk	
		fileOutput.close();
	}
}
