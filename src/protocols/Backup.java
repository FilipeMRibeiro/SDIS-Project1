package protocols;

import utilities.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;

public class Backup {

	private static final int chunkSize = 64; //in kBytes
	
	public Backup(String fileName, String fileDirectory, int repliDegree) {
		
		//TODO Test if the file exists
		//splitFile(fileName, fileDirectory);
	}
	
	//Helper Functions
	
	private static void splitFile(String fileDirectory, String fileName) {
		
		File file = new File(fileDirectory + fileName);

		//Handles the "File not found" Exception
		try {
			//Initialize input from file to read the data afterwards
			FileInputStream fileInput = new FileInputStream(file);
			BufferedInputStream inputBuffer = new BufferedInputStream(fileInput);
			
			//Buffer to store the data for each chunk
			byte[] fileDataBuffer = new byte[chunkSize * 1024]; //chunkSize is in kBytes, multiply by 1024 to convert to Bytes
			
			//Helper variable to check how many bytes we have read from the original file
			int bytesRead = 0; 
			
			//TODO Replace this with a new way to identify the files
			int fileNo = 1;

			//Cycle that reads the bytes from the file and creates a new file with it
			try { //Catches errors reading from original file or writing to new file
				while((bytesRead = inputBuffer.read(fileDataBuffer)) > 0 ) {
					String newFilePath = FileNameUtilities.stripExtension(file.getName()) + fileNo++ + FileNameUtilities.getExtension(file.getName());
					File newFile = new File(file.getParent(), newFilePath);
					
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
	
	private static String generateFileIdentifier(File file) {
		
		String fileIdentifier;
		Path path = file.toPath();
		
		try {
			BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, null);
			String lastModified = attributes.lastModifiedTime().toString();
			String created = attributes.creationTime().toString();
			String fileName = file.getName();
			
			fileIdentifier = created + "-" + fileName + "-" + lastModified;
			
		} catch (IOException e) {
			System.out.println("Error reading attributes from file " + file.getName());
			e.printStackTrace();
		}		
		return "";
	}
	
	public static void main(String[] args) {
		 splitFile("C:\\Users\\Grosso\\Desktop\\", "testfile.txt");
	}
}
