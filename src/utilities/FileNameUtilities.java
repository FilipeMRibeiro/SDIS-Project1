package utilities;

public class FileNameUtilities {

	
	public static String getExtension(String fileName) {

		int i = fileName.lastIndexOf('.');
		if (i > 0)
			fileName = fileName.substring(i);
		    
		return fileName;
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
	
}
