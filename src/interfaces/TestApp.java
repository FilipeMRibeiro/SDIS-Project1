package interfaces;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestApp {

	private static InetAddress ip_addr;
	private static int port_num;
	private static String sub_protocol;
	private static String path;
	private static int rep_degree;
	
	public static void main(String[] args) throws IOException {
		if(args.length != 3 && args.length != 4) {
			System.out.println("Error. Wrong number of args.");
			return;
		}
		String peer_ap = args[0];
		if(peer_ap.contains(":")) {
			ip_addr = InetAddress.getByName(args[0].split(":")[0]);
			port_num = Integer.parseInt(args[0].split(":")[1]);
		}
		else {
			ip_addr = InetAddress.getLocalHost();
			port_num = Integer.parseInt(args[0]);
		}
		sub_protocol = args[1];
		path = args[2];
		String request = "";
		
		if(sub_protocol.compareTo("BACKUP") == 0 && args.length == 4) {
			rep_degree = Integer.parseInt(args[3]);
			
			request += "BACKUP " + path + " " + rep_degree;
		}
		else if(args.length == 3) {
			if(sub_protocol.compareTo("RESTORE") == 0 || sub_protocol.compareTo("DELETE") == 0 || sub_protocol.compareTo("RECLAIM") == 0) {
				request += sub_protocol + " " + path;
			}
			else {
				System.out.println("Error. Protocol name is wrong or wrong number or args.");
				return;
			}
		}
		
		DatagramSocket socket = new DatagramSocket();
		byte[] buffer = new byte[256];
		buffer = request.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip_addr, port_num);
		socket.send(packet);
		
		boolean done = false;
		while(!done) {
			buffer = new byte[256];
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			
			String response = new String(packet.getData(), 0, packet.getLength());
			
			System.out.println("PEER'S RESPONSE: " + response);
			done = true;
		}
		
		socket.close();
		return;
	}
}
