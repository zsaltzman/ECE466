import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Math.*;

public class MovieTrafficGenerator {

	static int MAX_PACKET_SIZE = 1480;
    public static void main(String[] args) throws IOException {
		File fin = new File("movietrace.data");
        BufferedReader bis = new BufferedReader(new FileReader(fin));
        InetAddress localhost = InetAddress.getByName("localhost");
        String currentLine = null;
        DatagramPacket packet = null;
	
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


		try {
		    baseTime = System.nanoTime();

		    while((currentLine = bis.readLine()) != null && currentTime-baseTime < 120000000000l){
			st = new StringTokenizer(currentLine);
			seqNo = Integer.parseInt(st.nextToken());
			timestamp = Double.parseDouble(st.nextToken());
			type = st.nextToken();
			packetSize = Integer.parseInt(st.nextToken());

			timestamp = timestamp * 1000000;
		        
		  	byte[][] packets = makePacketBattery(packetSize);
			for (int i = 0; i < packets.length; i++) {
			    packet = new DatagramPacket(packets[i], packets[i].length, localhost, 4444);
			    socket.send(packet);
			}
			packetsSent++;
			lastSendTime = System.nanoTime();
			//spin until we should send the next packet
			currentTime = System.nanoTime();
			
			while(currentTime-lastSendTime < waitPeriod) {
			    currentTime = System.nanoTime();
			}
			
			//set the wait time to be the current time stamp minus the last timestamp and adjust to nanosecond granularity
			waitPeriod = (long)((timestamp-lastTimeStamp));
			lastTimeStamp = (long)(timestamp); 
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
		    
		    if (bis != null) {
			try {
			    bis.close();
			} catch (IOException e) {
			    System.out.println(e.getMessage());
			}
		    }
		}
    }
    
    public static byte[][] makePacketBattery(int num_bytes) {
	//we know ahead of time we're not going to get more than 1024 * 1000 bytes in a single send.
	int numPackets = (num_bytes/MAX_PACKET_SIZE);
	if (num_bytes % MAX_PACKET_SIZE != 0)
	    numPackets++;
	byte[][] packetBattery = new byte[numPackets][];
	int currentPacket = 0;
	while (num_bytes > 0) {
	    if(num_bytes < MAX_PACKET_SIZE)
		packetBattery[currentPacket] = new byte[num_bytes];
	    else
		packetBattery[currentPacket] = new byte[MAX_PACKET_SIZE];
	    
	    for (int i = 0; i < num_bytes && i < MAX_PACKET_SIZE; i++) {
			packetBattery[currentPacket][i] = 'a';
	    }
	    //set high priority
	    packetBattery[currentPacket][0] = (byte)2;
	    num_bytes -= MAX_PACKET_SIZE;
	    currentPacket++;
	}
	return packetBattery;
    }
}
