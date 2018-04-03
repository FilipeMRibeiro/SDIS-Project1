package utilities;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileNameUtilities {


	public static String generateFileIdentifier(File file) {

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
