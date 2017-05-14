import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class TrafficSink {

    public static void main(String[] args) throws IOException {

		DatagramSocket socket = new DatagramSocket(port);
		//TODO: make packet read number of bytes sent
		byte[] buf = new byte[1024];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		
		byte[] packetData;
		String seqNo = "";
        FileOutputStream outputStream = new FileOutputStream("outputSink.txt");
        PrintStream pout = new PrintStream(outputStream);

        long startTime = System.nanoTime();
        long currentTime;
	
	int packetsReceived = 0;
	try {
	    while(true) {
			socket.receive(p);
			packetsReceived++;

			//get sequence number from packet (since packets may have been dropped).
			//seqNo = new Int(p.getData());
			currentTime = System.nanoTime()-startTime;
			
			pout.println(fromByteArray(p.getData(), 2, 2) + " " + currentTime/1000  + " " + p.getLength());
			System.out.println("sink: port number " + fromByteArray(p.getData(), 0, 2) + " sequence number " + fromByteArray(p.getData(), 2, 2));
		    }
	} catch(IOException e) {
	    System.out.println(e.getMessage());
	} finally {
	    socket.close();
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
