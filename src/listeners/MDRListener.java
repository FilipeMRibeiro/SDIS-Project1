package listeners;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.Charset;

import peers.Peer;

public class MDRListener implements Runnable {

	private static String[] headerParts;
	private static String body;
	
	public void run() {
		while(true) {
			byte[] buf = new byte[256];
			DatagramPacket received = new DatagramPacket(buf, buf.length);
			
			try {
				StaticVariables.mdrSocket.receive(received);
				
				String message = new String(received.getData(), Charset.forName("ISO_8859_1"));
				
				if(processMessage(message)); //Receives the confirmation that the chunk was restored
				{
				}
				
			} catch (IOException e) {
				System.out.println("Data Restore Channel: " + "ERROR - " + e.getMessage());
			}
		}		
	}
	
	private static boolean processMessage(String message) {
		
		String header = message.split(StaticVariables.CRLF2)[0];
		String body = message.split(StaticVariables.CRLF2)[1];

		byte[] chunkData = body.getBytes(Charset.forName("ISO_8859_1")); //Solves some compatibility issues
		
		return restoreChunk(header, chunkData);
	}
	
	private static boolean restoreChunk(String header, byte[] chunkData) {
		
		headerParts = header.split(" ");
		String fileId = headerParts[3];
		String chunkNo = headerParts[4];
		
			
		return true;
	}
}
