package listeners;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.charset.Charset;

import peers.Peer;

public class MDBListener implements Runnable {

	
	public void run() {
		while(true){
			byte[] buf = new byte[StaticVariables.chunkSize * StaticVariables.k + 256]; //256 byts extra for the header of the message
			DatagramPacket received = new DatagramPacket(buf, buf.length);

			try {
				StaticVariables.mdbSocket.receive(received);				
				
				String message = new String(received.getData(), Charset.forName("ISO_8859_1"));
				
				if(processMessage(message)); //Receives the confirmation that the chunk was stored
				{
					
					String header = message.split(StaticVariables.CRLF2)[0];
					String[] headerParts = header.split(" ");
					String chunkNo = headerParts[4];
					String fileId = headerParts[3];
					
					Peer.sendStoredMessage(fileId, chunkNo);
					
				}
			} catch (IOException e) {
				System.out.println("Data Backup Channel: " + "ERROR - " + e.getMessage());
			} catch (InterruptedException e) {
				System.out.println("Delay error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	private static boolean processMessage(String message) {
		
		String header = message.split(StaticVariables.CRLF2)[0];
		
		String messageType = header.split(" ")[0];
		String senderId = header.split(" ")[2];
		if(!messageType.equals("PUTCHUNK") || senderId.equals(Peer.id))
			return false;
		
		String body = message.split(StaticVariables.CRLF2)[1];


		byte[] chunkData = body.getBytes(Charset.forName("ISO_8859_1")); //Solves some compatibility issues
		
		return storeChunk(header, chunkData);
		
		
	}
	
	private static boolean storeChunk(String header, byte[] chunkData) {
		
		
		
		String[] headerParts = header.split(" ");
		String fileId = headerParts[3];
		String chunkNo = headerParts[4];

		
		try {
			String working_dir = System.getProperty("user.dir");
			String path = working_dir + File.separator + "Stored Chunks" + File.separator + "Peer" + Peer.id + File.separator + fileId + File.separator +"Chunk" + chunkNo;
			File file = new File(path);
			file.getParentFile().mkdirs(); 
			file.createNewFile();	
			
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(chunkData);
			fileOutput.close();
		} catch (IOException e) {
			return false;
		}		
		return true;
	}
	
	
}