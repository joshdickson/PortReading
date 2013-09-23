/**
 * UDPPortReader.java
 * 
 * Copyright 2013 Joshua Dickson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package portreading;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import byteoperations.ByteOperations;

/**
 * A runnable class that provides a utility for reading from UDP broadcasts
 * @author Joshua Dickson
 * @version May 13, 2013
 * 			 - Initial build
 * @version July 22, 2013
 * 				Added finally block to ensure DatagramSocket null or closed on exit
 * @version July 26, 2013
 * 				Extracted a version from mainline code and adapted it for use as a debugging
 * 				application to strictly read the output from a port configuration. The application
 * 				now uses command line arguments to configure itself and responds with basic error 
 * 				messages to solve the most common problems.
 * @version September 23, 2013
 * 			 - Added support for an arbitrary, optional input of a delimiter
 * 			 - Updated licensing information
 */
public class UDPPortReader {
	
	private final int port;
	private final int bufferSize;
	private final String printConversion;
	private final String delimiter;
	
	/**
	 * Run the UDP port reader application
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		if(args.length < 3 || args.length > 4) {
			System.out.println("Usage: portNumber bufferSize hex/ascii (optional)delimiter");
			System.exit(1);
		} else if(args[2].equalsIgnoreCase("ascii") || args[2].equalsIgnoreCase("hex")) {
			UDPPortReader portReader;
			if(args.length == 3) {
				portReader = new UDPPortReader(Integer.parseInt(args[0]), 
						Integer.parseInt(args[1]), args[2], "");
			} else {
				portReader = new UDPPortReader(Integer.parseInt(args[0]), 
						Integer.parseInt(args[1]), args[2], args[3]);
			}
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
	 * @param port the port to read from
	 * @param bufferSize the maximum size to read
	 * @param printConversion the hex or ascii key
	 * @param delimiter the delimiter to use
	 */
	public UDPPortReader(int port, int bufferSize, String printConversion, String delimiter) {
		this.port = port;
		this.bufferSize = bufferSize;
		this.printConversion = printConversion;
		this.delimiter = delimiter;
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
			System.out.println(delimiter + System.currentTimeMillis() + " " +
					ByteOperations.convertByteArrayToASCIIString(byteData));
		} else if(printConversion.equalsIgnoreCase("hex")) {
			System.out.println(delimiter + System.currentTimeMillis() + " " +
					ByteOperations.convertByteArrayToHexString(byteData));
		}
	}


}

