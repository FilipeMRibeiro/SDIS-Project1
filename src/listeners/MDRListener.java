package listeners;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.Charset;

import peers.Peer;
import protocols.Restore;

public class MDRListener implements Runnable {

	private static String[] headerParts;
	private static String body;
	
	public void run() {
		while(true) {
			byte[] buf = new byte[StaticVariables.chunkSize * StaticVariables.k + 256];
			DatagramPacket received = new DatagramPacket(buf, buf.length);
			
			try {
				StaticVariables.mdrSocket.receive(received);
				
				String message = new String(received.getData(), Charset.forName("ISO_8859_1"));
								
				processMessage(message); //Receives the confirmation that the chunk was received


				
			} catch (IOException e) {
				System.out.println("Data Restore Channel: " + "ERROR - " + e.getMessage());
			}
		}		
	}
	
	private static boolean processMessage(String message) throws IOException {
		
		String header = message.split(StaticVariables.CRLF2)[0];
		
		String messageType = header.split(" ")[0];
		String senderId = header.split(" ")[2];
		
		if(!Peer.id.equals(Restore.requesterId)) 
			return false;
					
		if(!messageType.equals("CHUNK"))
			return false;
		
		String body = message.split(StaticVariables.CRLF2)[1];		
		
		byte[] chunkData = body.getBytes(Charset.forName("ISO_8859_1"));
		

		
		
		return addChunk(header, chunkData);
	}
	
	private static boolean addChunk(String header, byte[] chunkData) {
		
		String chunkNo = header.split(" ")[4];
		int chunkIndex = Integer.parseInt(chunkNo) - 1;
		System.out.println("Chunk index: " + chunkIndex);
		Restore.addChunk(chunkIndex, chunkData);
		
			
		return true;
	}
}
