import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class TrafficSink {
    public static void main(String[] args) throws IOException{
		DatagramSocket socket = new DatagramSocket(4445);
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
		if(packetsReceived == 1)
		    startTime = System.nanoTime();
		//get sequence number from packet (since packets may have been dropped).
		//seqNo = new Int(p.getData());
		currentTime = System.nanoTime()-startTime;
		
		pout.println(packetsReceived + " " + currentTime/1000  + " " + p.getLength());
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
}
