/**
 * HTTPPortReader.java
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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import byteoperations.ByteOperations;

/**
 * A runnable class that provides a utility for reading byte streams via the HTTP protocol
 * @author Joshua Dickson
 * @version October 4, 2013
 * 			 - 	Initial build
 * @version December 9, 2013
 * 			 -  Refactoring into NetworkReadable interface, general refactoring
 * @version February 2, 2014
 * 			 - Extracted a version from mainline code and adapted it for use as a stand alone
 * 			HTTP connection reader, allowing traces to be made of HTTP connection port data
 * 			to build test files. Added main method and command line options
 * 			 - Updated licensing information
 */
public class HTTPPortReader {

	private InputStream urlStream;
	private URLConnection urlConn;
	private final String HTTP_ADDRESS;
	private final boolean shouldRun;
	private final int readSize;
	
	/**
	 * Run the HTTP port reader application
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage: httpAddress sampleSize");
			System.exit(1);
		} else {
			HTTPPortReader reader = new HTTPPortReader(args[0], Integer.parseInt(args[1]));
			reader.read();
		}
	}
	
	/**
	 * Construct an HTTPPortReader that will read an HTTP stream from a given address
	 * @param httpAddress the address that will be connected to
	 * @param outputQueue the queue from which generated packets will be placed
	 */
	public HTTPPortReader(String httpAddress, int readSize) {
		super();
		this.readSize = readSize;
		this.HTTP_ADDRESS = httpAddress;
		shouldRun = true;
	}
	

	/**
	 * Read data from the port
	 */
	public void read() {
		tryConnectionSetup();
		while(shouldRun) {
			try {
				while(shouldRun) {
					byte[] payload = new byte[readSize];
					byte[] cleaned = reduce(payload, urlStream.read(payload));
					if(cleaned != null) { 
						System.out.print("DATADATADATA");
						System.out.print(System.currentTimeMillis());
						System.out.print(" ");
						System.out.println(ByteOperations.convertByteArrayToHexString(cleaned));
					}
				}
			} catch(Exception ex) {
				System.out.println("Error.");
				System.exit(1);
			}
		}
	}	

	/**
	 * Try to do the connection set up, timing out and retrying continuously
	 */
	private void tryConnectionSetup() {
		System.out.println("Trying");
		boolean reestabConnection = false;
		while(!reestabConnection && shouldRun) {
			try {
				urlConn = new URL(HTTP_ADDRESS).openConnection();
				urlConn.setReadTimeout(100);
				urlConn.setConnectTimeout(100);
				urlConn.connect();
				urlStream = urlConn.getInputStream();
				reestabConnection = true;
			} catch(Exception exe) {				
				try {
					TimeUnit.MILLISECONDS.sleep(2000);
				} catch (InterruptedException e) {
					// OK
				}
			}
		}
	}
	
	/**
	 * Reduce an array of bytes with a number of relevant bytes of the full array
	 * @return
	 */
	protected byte[] reduce(byte[] array, int size) {
		if(size < 0) return null;
		byte[] reduced = new byte[size];
		System.arraycopy(array, 0, reduced, 0, size);
		return reduced;
	}

				
			
}
