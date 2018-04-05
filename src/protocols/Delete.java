package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;

import listeners.StaticVariables;
import peers.Peer;
import utilities.FileNameUtilities;

public class Delete {

	public static void initiateProtocol(String fileName, String filePath) throws IOException {
		
		String path = filePath + File.separator + fileName;
		File file = new File(path);

		String fileId = FileNameUtilities.generateFileIdentifier(file);
		
		String message = "DELETE" + " " + StaticVariables.version + " " + Peer.id + " " + fileId + " " + StaticVariables.CRLF2;	
		DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), StaticVariables.mcAddress, StaticVariables.mcPort);
		StaticVariables.mcSocket.send(messagePacket);
		
	}
	
	public static void deleteFile(String fileId) {
		String path = "Stored Chunks" + "/" + "Peer" + Peer.id + "/" + fileId;
		File file = new File(path);
		
		if(!file.exists())
			return;
		
		String[] entries = file.list();
		for(String s: entries) {
			File currentFile = new File(file.getPath(), s);
			currentFile.delete();
		}
		
		file.delete();
		
	}
	
	
	
	public static void test() throws IOException {
		String dir = "C:\\Users\\Grosso\\Desktop";
		String name = "test2.txt";
				

		
		initiateProtocol(name, dir);
	}
	
}
