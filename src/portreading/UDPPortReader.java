/**************************************************************************************************
 * UDPPortReader.java
 * Joshua Dickson for Autoliv, Inc.
 * All Rights Reserved.
 *************************************************************************************************/
package portreading;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import byteoperations.ByteOperations;

/**
 * A runnable class that provides a utility for reading from UDP broadcasts
 * @author Joshua Dickson
 * @version May 13, 2013
 * @version July 22, 2013
 * 				Added finally block to ensure DatagramSocket null or closed on exit
 * @version July 26, 2013
 * 				Extracted a version from mainline code and adapted it for use as a debugging
 * 				application to strictly read the output from a port configuration. The application
 * 				now uses command line arguments to configure itself and responds with basic error 
 * 				messages to solve the most common problems.
 */
public class UDPPortReader {
	
	private final int port;
	private final int bufferSize;
	private final String printConversion;
	
	/**
	 * Run the UDP port reader application
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("Usage: portNumber bufferSize hex/ascii");
			System.exit(1);
		} else if(args[2].equalsIgnoreCase("ascii") || args[2].equalsIgnoreCase("hex")) {
			UDPPortReader portReader = new UDPPortReader(Integer.parseInt(args[0]), 
					Integer.parseInt(args[1]), args[2]);
			portReader.read();
		} else {
			System.out.println("Error.");
			System.exit(1);
		}
	}
	
	/**
	 * Constructs a UDPPortReader that will read from the given port on the local machine
	 * into a buffer with the given size, and place a List representation into the given
	 * queue.
	 * @param port
	 * @param bufferSize
	 * @param outputQueue
	 */
	public UDPPortReader(int port, int bufferSize, String printConversion) {
		this.port = port;
		this.bufferSize = bufferSize;
		this.printConversion = printConversion;
	}
    
    /**
     * Read from the port
     */
    public void read() {
		DatagramSocket dsocket = null;
		try {
    		dsocket = new DatagramSocket(port);
    		byte[] buffer = new byte[bufferSize];
    		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    		while(true) {
    			dsocket.receive(packet);
    			byte[] byteData = packet.getData(); 
    			printData(byteData);			
    			packet.setLength(buffer.length);
    		}
    	} catch (Exception ex) {
    		System.err.println("Connection not available.");
    	} finally {
    		if(dsocket != null && !dsocket.isClosed()) {
    			dsocket.close();
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

