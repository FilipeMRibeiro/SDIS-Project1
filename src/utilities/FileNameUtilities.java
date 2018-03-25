package utilities;

public class FileNameUtilities {

	
	public static String getExtension(String fileName) {

		//Gets the last dot in the file name
		int i = fileName.lastIndexOf('.');
		if (i > 0)
			fileName = fileName.substring(i);
		    
		return fileName; //with dot (e.g. for "filename.txt" returns ".txt")
	}
	
	public static String stripExtension(String fileName) {
        // Handle null case specially.
        if (fileName == null) return null;

        // Get position of last '.'.
        int pos = fileName.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return fileName;

        // Otherwise return the string, up to the dot.
        return fileName.substring(0, pos);
    }
	
	public static String bytesToHex(byte[] hash) {
		
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    	String hex = Integer.toHexString(0xff & hash[i]);
	    	if(hex.length() == 1) hexString.append('0');
	        	hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
}
