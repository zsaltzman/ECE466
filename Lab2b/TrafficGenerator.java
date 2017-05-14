import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Math.*;

public class TrafficGenerator {
    public static void main(String[] args) throws IOException {	
		FileOutputStream outputStream = new FileOutputStream("outputGenerator.txt");
	    PrintStream pout = new PrintStream(outputStream);
	    InetAddress localhost = InetAddress.getByName("localhost");
	    String currentLine = null;
	    DatagramPacket packet = null;
		
		long T;
		int L, N; 

		DatagramSocket socket = new DatagramSocket();
		byte[] buf;
		
		long lastSendTime = 0;
	    long startTime = 0;
	    long currentTime = 0;
		long waitPeriod = 0;
		long lastTimeStamp = 0;
		long baseTime = 0;
		long longTimestamp = 0;
		
	    StringTokenizer st;
		int packetsSent = 0;
		
		int seqNo;
		double timestamp;
		int packetSize;
		String type;

		//1Mbps
		T = 100000 * 8 *2; //T is in nanoseconds
		N = 1;
		L = 100;

		baseTime = System.nanoTime();
		try {
		    while(true) {  

		  	byte[][] packets = makePacketBattery(L,N);
			for (int i = 0; i < packets.length; i++) {
				currentTime = System.nanoTime();
			    packet = new DatagramPacket(packets[i], packets[i].length, localhost, 4444);
			    socket.send(packet);
			   	pout.println(packetsSent + " " + (currentTime-baseTime)/1000  + " " + packets[i].length);
				packetsSent++;
			}

			while(currentTime-lastSendTime < T) {
			    currentTime = System.nanoTime();
			}
		        
			//we take the time here to account for the runtime of our code
			lastSendTime = System.nanoTime();
		    }
		}
		catch(IOException e) {
		    System.out.println(e.getMessage());
		}
		finally {
		    //signal receiver to close
		    String eof = "done";
		    packet = new DatagramPacket(eof.getBytes(), eof.length(), localhost, 1111);
		    socket.send(packet);
		    try {
			    outputStream.close();
			    pout.close();
			} catch (IOException e) {
			    System.out.println(e.getMessage());
			}
	    }
    }
    public static byte[][] makePacketBattery(int packetSize, int numPackets) {
		//we know ahead of time we're not going to get more than 1024 * 1000 bytes in a single send.
		byte[][] packetBattery = new byte[numPackets][];
		for (int i = 0; i<numPackets; i++) {
			packetBattery[i] = new byte[packetSize];
			for(int j = 0; j < packetSize; j++) {
				packetBattery[i][j] = 'a';
			}
		}
		return packetBattery;
	}
}
