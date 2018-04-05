package protocols;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import listeners.StaticVariables;
import peers.Peer;
import utilities.FileNameUtilities;

public class Restore {

	private static byte[][] chunks;
	private static List<Integer> chunksReceivedIndexes;
	private static String fileName;
	private static int chunksReceived;
	public static String requesterId;

	public static void initiateProtocol(String fileName, String filePath) throws IOException {

		Restore.fileName = fileName;

		String path = filePath + File.separator + fileName;
		File file = new File(path);

		String fileId = FileNameUtilities.generateFileIdentifier(file);

		
		FileInputStream fileInput = new FileInputStream(file);
		long syze = fileInput.getChannel().size();
		fileInput.close();

		
		int chunksNeeded = (int) Math.ceil(syze / (double)(StaticVariables.chunkSize * StaticVariables.k));

		System.out.println("Chunks needed: " + chunksNeeded);

		chunks = new byte[chunksNeeded][StaticVariables.chunkSize * StaticVariables.k];
		chunksReceivedIndexes = new ArrayList<Integer>();
		chunksReceived = 0;
		while(chunksReceived < chunksNeeded)
		{
			getChunk(fileId, chunksReceived + 1);

			//waits for responses for 1 second
			long startTime = System.currentTimeMillis();
			boolean proceed = false;
			
			while(!proceed) {
				long curTime = System.currentTimeMillis();
				if(curTime - startTime >= 1000)
					proceed = true;
			}
		}
		restoreFile();
	}

	private static void getChunk(String fileId, int chunkNo) throws IOException {

		String message = "GETCHUNK" + " " + StaticVariables.version + " " + Peer.id + " " + fileId + " " + chunkNo + " " + StaticVariables.CRLF2;	
		DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), StaticVariables.mcAddress, StaticVariables.mcPort);
		StaticVariables.mcSocket.send(messagePacket);

	}

	private static void restoreFile() throws IOException{
		String working_dir = System.getProperty("user.dir");
		String path = working_dir + File.separator + "Restored Chunks" + File.separator + fileName;

		File restoredFile = new File(path);
		restoredFile.getParentFile().mkdirs(); 
		restoredFile.createNewFile();

		FileOutputStream fileWriter = new FileOutputStream(restoredFile);		

		for(int i = 0; i < chunks.length; ++i)
			fileWriter.write(chunks[i]);		

		fileWriter.close();
	}



	public static void sendChunk(String fileId, String chunkNo) throws IOException, InterruptedException {
		String working_dir = System.getProperty("user.dir");
		String path = working_dir + File.separator + "Stored Chunks" + File.separator + "Peer" + Peer.id + File.separator + fileId + File.separator +"Chunk" + chunkNo;

		File chunk = new File(path);
		if(!chunk.exists())
			System.out.println("Peer does not possess this chunk");
		else
		{
			FileInputStream chunkInput = new FileInputStream(chunk);
			byte[] chunkData = new byte[StaticVariables.chunkSize * StaticVariables.k];
			chunkInput.read(chunkData);
			
			chunkInput.close();

			String dataString = new String(chunkData, Charset.forName("ISO_8859_1"));

			String message = "CHUNK" + " " + StaticVariables.version + " " + Peer.id + " " + fileId + " " + chunkNo + StaticVariables.CRLF2 + dataString;
			
			DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), StaticVariables.mdrAddress, StaticVariables.mdrPort);

			//Random Delay from 0 to 400ms to avoid congestion
			Random rand = new Random();
			int delay = rand.nextInt(400) + 1;
			Thread.sleep(delay);

			StaticVariables.mdrSocket.send(messagePacket);

		}		

	}

	public static void addChunk(int chunkIndex, byte[] chunkData) {
		
		if(!chunksReceivedIndexes.contains(chunkIndex)) {
			chunksReceivedIndexes.add(chunkIndex);
			
			for(int i = 0; i < chunks.length; ++i)
				chunks[chunkIndex] = chunkData;
			
			
			++chunksReceived;
		}
	}

	public static void test() throws IOException{
		String dir = "C:\\Users\\Grosso\\Desktop";
		String name = "test.jpg";
		initiateProtocol(name, dir);
	}

}
