package listeners;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.charset.Charset;

import peers.Peer;

public class MDBListener implements Runnable {

	private static String[] headerParts;
	
	public void run() {
		while(true){
			byte[] buf = new byte[StaticVariables.chunkSize * StaticVariables.k + 256]; //256 byts extra for the header of the message
			DatagramPacket received = new DatagramPacket(buf, buf.length);

			try {
				StaticVariables.mdbSocket.receive(received);				
				
				String message = new String(received.getData(), Charset.forName("ISO_8859_1"));
				if(processMessage(message)); //Receives the confirmation that the chunk was stored
				{
					Peer.chunkNo = headerParts[4];
					Peer.senderId = headerParts[2];
				}
			} catch (IOException e) {
				System.out.println("Data Backup Channel: " + "ERROR - " + e.getMessage());
			}
		}
	}
	
	
	private static boolean processMessage(String message) {
		
		String header = message.split(StaticVariables.CRLF2)[0];
		String body = message.split(StaticVariables.CRLF2)[1];

		byte[] chunkData = body.getBytes(Charset.forName("ISO_8859_1")); //Solves some compatibility issues
		
		return storeChunk(header, chunkData);
		
		
	}
	
	private static boolean storeChunk(String header, byte[] chunkData) {
		
		headerParts = header.split(" ");
		String fileId = headerParts[3];
		String chunkNo = headerParts[4];

		
		try {
			String path = "Stored Chunks" + "//" + fileId + "//" +"Chunk" + chunkNo;
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
	
	
	
	public static void main(String[] args) throws IOException{
		
		File file = new File("C:" + File.separator + "Users" + File.separator + "Utilizador" + File.separator + "Desktop" + File.separator + "test.jpg");
		FileInputStream fileInput = new FileInputStream(file);
		
		byte[] bytes = new byte[1000000];
		
		int readBytes = fileInput.read(bytes);
		
		
		String message = "PUTCHUNK <Version> <SenderId> FileId2 2 <ReplicationDeg> " + StaticVariables.CRLF2 + new String(bytes, 0, readBytes, Charset.forName("ISO_8859_1"));
		
		
		processMessage(message);
		
	}
	
	
}
