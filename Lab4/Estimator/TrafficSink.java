package Estimator;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class TrafficSink implements Runnable {
	int port;

	public TrafficSink(int _port) {
		port = _port;
	}

	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			startSink(socket);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) 
				socket.close();
		}
	}
    public void startSink(DatagramSocket socket) throws IOException {
		//TODO: make packet read number of bytes sent
		byte[] buf = new byte[1024];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		boolean receiving = true;
		boolean receivedData = false; 

		byte[] packetData;
		String seqNo = "";
        FileOutputStream outputStream = null;
        PrintStream pout = null;

        long startTime = System.nanoTime();
        long currentTime;
		String fileStreamName;
		int currentFileStream = 0;

		while (true) {
			//filter out fins from previous streams
			while(!receivedData) {
				socket.receive(p);
				if (fromByteArray(p.getData(), 2, 2) != 65535) {
					currentFileStream++;
					fileStreamName = "outputSink" + currentFileStream + ".txt"; 
					outputStream = new FileOutputStream(fileStreamName);
					pout = new PrintStream(outputStream);

					currentTime = System.nanoTime()-startTime;
					pout.println(fromByteArray(p.getData(), 2, 2) + " " + currentTime/1000 + " " + p.getLength());
					receivedData = true;
				}
			}
		
			int packetsReceived = 1;
			try {
			    while(receiving) {
					socket.receive(p);
					packetsReceived++;

					//stop receiving if we get a fin
					if (fromByteArray(p.getData(), 2, 2) == 65535) 
						receiving = false;
					else
						receivedData = true;

					//get sequence number from packet (since packets may have been dropped).
					//seqNo = new Int(p.getData());
					currentTime = System.nanoTime()-startTime;
					
					pout.println(fromByteArray(p.getData(), 2, 2) + " " + currentTime/1000  + " " + p.getLength());
					//System.out.println("sink: port number " + fromByteArray(p.getData(), 0, 2) + " sequence number " + fromByteArray(p.getData(), 2, 2));
				    }
			} catch(IOException e) {
			    System.out.println(e.getMessage());
			} finally {
				System.out.println("received stop signal");
				receiving = true;
				receivedData = false;
			    if (outputStream != null && pout != null) {
					try {
					    outputStream.close();
					    pout.close();
					} catch (IOException e) {
					    System.out.println(e.getMessage());
					}
			    }
			} 
		} 
    }
    /**
	* Converts a byte array to an integer.
	* @param value a byte array
	* @param start start position in the byte array
	* @param length number of bytes to consider
	* @return the integer value
	*/
	public static int fromByteArray(byte [] value, int start, int length) {
		int Return = 0;
		for (int i=start; i< start+length; i++) {
			Return = (Return << 8) + (value[i] & 0xff);
		}
		return Return;
	}
}
