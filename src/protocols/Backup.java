package protocols;

import utilities.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import controllers.PeerController;

public class Backup {

	private static final int chunkSize = 64; //in kBytes
	private static final int k = 1000; //if we wish to easily convert from kBytes to kiBytes
	private static String mcast_addr = "225.0.0";
	private static int mcast_port = 8000;
	
	public Backup(String fileName, String fileDirectory, int repliDegree) {
		
		//TODO Test if the file exists
		//splitFile(fileName, fileDirectory);
	}
	
	//Helper Functions
	
	private static void splitFile(String fileDirectory, String fileName) throws IOException {
		
		File file = new File(fileDirectory + fileName);
		
		InetAddress multicast_group = InetAddress.getByName(mcast_addr);
		MulticastSocket multicast_socket = new MulticastSocket(mcast_port);
		multicast_socket.joinGroup(multicast_group);

		//Handles the "File not found" Exception
		try {
			//Initialize input from file to read the data afterwards
			FileInputStream fileInput = new FileInputStream(file);
			BufferedInputStream inputBuffer = new BufferedInputStream(fileInput);
			
			//Buffer to store the data for each chunk
			byte[] fileDataBuffer = new byte[chunkSize * k];
			
			//Helper variable to check how many bytes we have read from the original file
			int bytesRead = 0; 
			
			//TODO Replace this with a new way to identify the files
			int fileNo = 0;
			//Cycle that reads the bytes from the file and creates a new file with it
			try { //Catches errors reading from original file or writing to new file
				while((bytesRead = inputBuffer.read(fileDataBuffer)) > 0 ) {
					//String newFilePath = FileNameUtilities.stripExtension(file.getName()) + fileNo++ + FileNameUtilities.getExtension(file.getName());
					String newFileName = generateFileIdentifier(file, fileNo++);
					String fileNumber = "" + fileNo;
					
					File newFile = new File(file.getParent(), newFileName);
					
					//Sends file no
					DatagramPacket packet = new DatagramPacket(fileNumber.getBytes(), fileNumber.getBytes().length, multicast_group, mcast_port);
					multicast_socket.send(packet);
					
					//Sends filename
					packet = new DatagramPacket(newFileName.getBytes(), newFileName.getBytes().length, multicast_group, mcast_port);
					multicast_socket.send(packet);
					
					//Sends file content
					packet = new DatagramPacket(fileDataBuffer, fileDataBuffer.length, multicast_group, mcast_port);
					multicast_socket.send(packet);
					
					//Opens filestream to output the copied data to a new file of size = chunkSize
					FileOutputStream fileOutput = new FileOutputStream(newFile);
					fileOutput.write(fileDataBuffer, 0, bytesRead - 1); // bytesRead is used to make sure that all chunks only get relevant data. This avoids the last chunk having jitter at the end of it caused by data from the previous chunk	
					fileOutput.close();
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
	
	private static String generateFileIdentifier(File file, int fileNo) {
		
		String fileIdentifier = file.getName(); //Just an initialization of the variable fileIdentifier
		Path path = file.toPath();
		
		String lastModified = String.valueOf(file.lastModified());
		String size = String.valueOf(file.getTotalSpace());
		String fileName = file.getName();

		fileIdentifier = fileName + "-" + size + "-" + lastModified + "-" + String.valueOf(fileNo);

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
	
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.out.println("Usage: java protocols.Backup <mcast_addr>");
			return;
		}
		mcast_addr = args[0];
		 //splitFile("C:\\Users\\Grosso\\Desktop\\", "testfile.txt");
		 splitFile("/home/filipe/Documents/", "testfile.txt");
	}
}
