package listeners;

import java.net.InetAddress;
import java.net.MulticastSocket;

public class StaticVariables {
	


	public static final String CRLF = "\r\n";
	public static final String CRLF2 = CRLF + CRLF;

	public static final int chunkSize = 64; //in kBytes
	public static final int k = 1000; //if we wish to easily convert from kBytes to kiBytes



	public static MulticastSocket mcSocket;
	public static MulticastSocket mdbSocket;
	public static MulticastSocket mdrSocket;
	
	public static InetAddress mcAddress;
	public static InetAddress mdbAddress;
	public static InetAddress mdrAddress;
	
	public static int mcPort;
	public static int mdbPort;
	public static int mdrPort;
	
	public static MCListener mcListener;
	public static MDRListener mdrListener;	
	public static MDBListener mdbListener;	
}
