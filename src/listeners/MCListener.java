package listeners;

import java.io.IOException;
import java.net.DatagramPacket;

import peers.Peer;
import protocols.Backup;

public class MCListener implements Runnable {

	public void run() {
		while(true){
			byte[] buf = new byte[256];
			
			DatagramPacket receiveDatagram = new DatagramPacket(buf, buf.length);
			
			try {
				StaticVariables.mcSocket.receive(receiveDatagram);
			
				
				String receivedMessage = new String(buf, 0, buf.length).trim();			
				
				System.out.println("MC: " + receivedMessage);
				
				String[] receivedArgs = receivedMessage.split(" ");
				
				String messageType = receivedArgs[0];
				
				switch(messageType){
					case "STORED":
						receivedStoredMessage(receivedArgs);
						break;

					default:
						break;		
					}
				
			} catch (IOException e) {
				System.out.println("Control Channel: " + "ERROR: " + e.getMessage());
			}
		}
	}
	
	private static void receivedStoredMessage(String[] receivedArgs) {
		
		String peerId = receivedArgs[2];
		String fileId = receivedArgs[3];
		String chunkNo = receivedArgs[4];

		Backup.receivedResponse(peerId, fileId, chunkNo);
	}

}
