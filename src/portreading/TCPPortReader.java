/**************************************************************************************************
 * TCPPortReader.java
 * Joshua Dickson for Autoliv, Inc.
 * All Rights Reserved.
 *************************************************************************************************/
package portreading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import byteoperations.ByteOperations;

/**
 * A runnable utility class that provides functionality to read from TCP sockets
 * @author Joshua Dickson
 * @version May 23, 2013
 * @version May 26, 2013
 * 				Added revised timeout mechanism and bug fix to Socket connect so a bad connection
 * 				does not cause the run() loop to hang waiting for a connection.
 * @version July 16, 2013
 * 				Modified the input to return a byte array instead of a String
 * @version July 24, 2013
 * 				Fixed a bug in a new use of System.arrayCopy that attempted to copy the entire buffer
 * 				into the final byte data array to be passed to the output queue. The copy call now
 * 				only reads the number of bytes that were available at read instead of the full buffer
 * @version July 26, 2013
 * 				Extracted a version from the mainline code and adapted it for use as a debugging
 * 				application to strictly read the output from a port configuration. The application
 * 				now uses command line arguments to configure itself and responds with basic error
 * 				messages to solve the most common problems.
 */
public class TCPPortReader {
	
	private final String socketAddress;
	private final int port;
	private final int bufferSize;
	private final String printConversion;
	
	/**
	 * Run the TCP port reader application
	 * @param args the input arguments
	 */
	public static void main(String[] args) {		
		if(args.length < 4) {
			System.out.println("Usage: bindAddress portNumber bufferSize hex/ascii");
			System.exit(1);
		} else if(args[3].equalsIgnoreCase("ascii") || args[3].equalsIgnoreCase("hex")) {
			TCPPortReader portReader = new TCPPortReader(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
			portReader.read();
		} else {
			System.out.println("Error.");
			System.exit(1);
		}
	}
	
	/**
	 * Construct a TCPPortReader with the given connection and socket attributes
	 * @param socketAddress the IP address to connect the socket to
	 * @param port the port on which to open the socket
	 * @param outputQueue the queue to place generated objects into
	 */
	public TCPPortReader(String socketAddress, int port, int bufferSize, String printConversion) {
		this.socketAddress = socketAddress;
		this.port = port;
		this.bufferSize = bufferSize;
		this.printConversion = printConversion;
	}
	
	/**
	 * Read from the port
	 */
	public void read() {
		Socket skt;
		BufferedReader in = null;
		InputStream is = null;
		try {
			skt = new Socket();
			skt.setSoTimeout(500);
			skt.connect(new InetSocketAddress(socketAddress, port), 500);
			is = skt.getInputStream();
			while(true) {
				byte[] data = new byte[bufferSize];
				int dataSize = is.read(data);				
				if(dataSize > -1) {
					byte[] dataActual = new byte[dataSize];
					System.arraycopy(data, 0, dataActual, 0, dataSize);
					printData(dataActual);
				} 
			}
		} catch(Exception ex) {
			System.err.println("Connection not available.");
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Print the data array using the conversion type
	 * @param byteData the byte data to print
	 */
	private void printData(byte[] byteData) {
		if(printConversion.equalsIgnoreCase("ascii")) {
			System.out.println("DATADATADATA" + System.currentTimeMillis() + " " +
					ByteOperations.convertByteArrayToASCIIString(byteData));
		} else if(printConversion.equalsIgnoreCase("hex")) {
			System.out.println("DATADATADATA" + System.currentTimeMillis() + " " +
					ByteOperations.convertByteArrayToHexString(byteData));
		}
	}
	
	
}
