package protocols;

import java.io.*;

public class Restore {

	public static void restoreFile(String fileName, String filePath, byte[][] chunks) throws IOException{
				
		String path = filePath + File.separator + fileName;
	
		File restoredFile = new File(path);
		restoredFile.getParentFile().mkdirs(); 
		restoredFile.createNewFile();
		
		FileOutputStream fileWriter = new FileOutputStream(restoredFile);		
		
		for(int i = 0; i < chunks.length; ++i)
			fileWriter.write(chunks[i]);		
		
		fileWriter.close();
	}
	
	
	
}
