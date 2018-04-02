package protocols;

import utilities.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import controllers.PeerController;
import listeners.StaticVariables;
import peers.Peer;

public class Backup {

	static String fileId;
	
	private static void backupFile(String fileDirectory, String fileName, int replicationDegree) throws IOException {
		
		File file = new File(fileDirectory + "\\" + fileName);
		
		fileId = generateFileIdentifier(file);

		//Handles the "File not found" Exception
		try {
			//Initialize input from file to read the data afterwards
			FileInputStream fileInput = new FileInputStream(file);
			BufferedInputStream inputBuffer = new BufferedInputStream(fileInput);
			
			//Buffer to store the data for each chunk
			byte[] fileDataBuffer = new byte[StaticVariables.chunkSize * StaticVariables.k];
			
			//Helper variable to check how many bytes we have read from the original file
			int bytesRead = 0; 
			
			//TODO Replace this with a new way to identify the files
			int chunkNo = 1;
			//Cycle that reads the bytes from the file and creates a new file with it
			try { //Catches errors reading from original file or writing to new file
				while((bytesRead = inputBuffer.read(fileDataBuffer)) > 0 ) {
					
					
					sendMessage(fileDataBuffer, bytesRead, chunkNo++, replicationDegree);
					
					//WAITS FOR RESPONSE
					
				}
			} catch (IOException e) {
				System.out.println("Error reading from file " + fileName + " in directory: " + fileDirectory);
				e.printStackTrace();
			}			
			
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " not found in directory: " + fileDirectory); 
			e.printStackTrace();
		}
			
	}
	
	
	
	private static void sendMessage(byte[] data, int bytesRead, int chunkNo, int replicationDegree) throws IOException{
		
		String dataString = new String(data, Charset.forName("ISO_8859_1"));
		
		String message = "PUTCHUNK" + " " + StaticVariables.version + " " + Peer.id + " " + fileId + " " + chunkNo + " " + replicationDegree + " " + StaticVariables.CRLF2 + dataString;
		
		DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), StaticVariables.mdbAddress, StaticVariables.mdbPort);
		StaticVariables.mdbSocket.send(messagePacket);
	}
	
	
	
	
	
	
	private static String generateFileIdentifier(File file) {
		
		String fileIdentifier = file.getName(); //Just an initialization of the variable fileIdentifier
		Path path = file.toPath();
		
		String lastModified = String.valueOf(file.lastModified());
		String size = String.valueOf(file.getTotalSpace());
		String fileName = file.getName();

		fileIdentifier = fileName + "-" + size + "-" + lastModified + "-";

		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(fileIdentifier.getBytes(StandardCharsets.UTF_8));

			fileIdentifier = FileNameUtilities.bytesToHex(encodedhash);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	
		return fileIdentifier;
	}
	
	public static void test() throws IOException{
		String dir = "C:\\Users\\Grosso\\Desktop";
		String name = "test.txt";
		backupFile(dir, name, 1);
	}

}
