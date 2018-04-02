package listeners;

import java.io.IOException;
import java.net.DatagramPacket;

import peers.Peer;

public class MCListener implements Runnable {

	public void run() {
		while(true){
			byte[] buf = new byte[256];
			
			DatagramPacket receiveDatagram = new DatagramPacket(buf, buf.length);
			
			try {
				StaticVariables.mcSocket.receive(receiveDatagram);
				
				String receivedMessage = new String(buf, 0, buf.length).trim();				
				String[] receivedArgs = receivedMessage.split("\\s+");
				
				String messageType = receivedArgs[1];
				
				switch(messageType){
					case "STORED":
						
						break;

					default:
						break;		
					}
				
			} catch (IOException e) {
				System.out.println("Control Channel: " + "ERROR: " + e.getMessage());
			}
		}
	}

}
