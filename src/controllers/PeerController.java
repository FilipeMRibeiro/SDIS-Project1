package controllers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class PeerController {

	private static String mcast_addr;
	private static int mcast_port;
	
	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.out.println("Error. Usage.");
			return;
		}
		
		mcast_addr = args[0];
		mcast_port = Integer.parseInt(args[1]);
		
		//Open MulticastSocket
		MulticastSocket multicast_socket = new MulticastSocket(mcast_port);
		InetAddress multicast_address = InetAddress.getByName(mcast_addr);
		multicast_socket.joinGroup(multicast_address);
		
		boolean done = false;
		while(!done) {
			byte[] buffer = new byte[256];
			DatagramPacket multicast_packet = new DatagramPacket(buffer, buffer.length);
			multicast_socket.receive(multicast_packet);
			String advertisement = new String(multicast_packet.getData());
			String[] parsed = advertisement.split("&");
			String newPeer_id = parsed[0];
			int newPeer_port = Integer.parseInt(parsed[1].replaceAll("[^\\d.]", ""));
			
			String ad_response = "registered";
			multicast_packet = new DatagramPacket(ad_response.getBytes(), ad_response.getBytes().length, multicast_address, mcast_port);
			System.out.println("Peer " + newPeer_id + " is at port " + newPeer_port);
		}
	}
}
