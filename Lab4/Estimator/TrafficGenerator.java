package Estimator;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Math.*;


//ip address is args[0], port num is args[1]
public class TrafficGenerator implements Runnable {
	private static final int TRAINS_IN_PROBE = 10;

	int boxPortNum;
	int destPortNum;
	String ipAddress; 
	int N, L, r;

	public TrafficGenerator(int _boxPortNum, int _destPortNum, String _ipAddress, int _N, int _L, int _r) {
		boxPortNum = _boxPortNum;
		destPortNum = _destPortNum;
		ipAddress = _ipAddress;
		N = _N;
		L = _L;
		r = _r;
	}

	//NOTE: 
	//to run this code for exercise 1.5, replace the call to probe() with sendPackets(x)
	//where x is the desired speed
	public void run() {
		try {
			probe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void probe() {
		long currentTime;
		long baseTime;

		StringTokenizer st;
		int currentIndex = 0; 
		String currentLine;
		int seqNo;
		double timestamp;
		int cumulative_arrivals;

		int currentFile = 1; 

		ArrayList rates = new ArrayList();
		double currentRate = r; //start probing at r kbps
		double lastRate = -1;

		long[][] departures = null;
		long bmax_p = -1;

		long bmax = 0;
		long backlog_t = 0;
		long departures_t = 0;
		int departures_index = 0;
		int index = 0;

		File fin;
        BufferedReader bis;
		while(true) {
			System.out.println("probing at " + currentRate + " kbps");
			cumulative_arrivals = 0;
			try {
				departures = sendPackets(currentRate);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				fin = new File("outputSink" + currentFile + ".txt");
				bis = new BufferedReader(new FileReader(fin));

				//wait a second for sink to finish writing
				baseTime = currentTime = System.nanoTime();
				while (currentTime-baseTime < 1000000000) {
					currentTime = System.nanoTime();
				}

				//read the output file to get departures
				while ((currentLine = bis.readLine()) != null) {
					st = new StringTokenizer(currentLine);
					seqNo = Integer.parseInt(st.nextToken());
					timestamp = Double.parseDouble(st.nextToken());
					cumulative_arrivals = cumulative_arrivals + Integer.parseInt(st.nextToken());
					while(timestamp < departures_t) {
						departures_t = departures[index][1];
						index++;
					}
					backlog_t = departures[index][0] - cumulative_arrivals;
					index = 0;

					//if the current backlog is greater than the max backlog we've found so far, set it to the current backlog.
					if (backlog_t > bmax) 
						bmax = backlog_t;
					
					departures_t = 0;				
				}
				//determine whether service curve has improved 
				//with the method specified in the handout, we stop when the backlog for this probe is double the backlog from the last probe
				//i.e. assuming constant time scale, backlog grows at a rate greater than rt, demonstrating the black box is at capacity.
				if (bmax_p != -1 && bmax >= bmax_p * 2) {
					System.out.println("service curve estimate getting no better, stopping.");
					System.out.println("rate = " + lastRate + " delay = " + bmax_p/lastRate);
					break;
				}
				else {
					System.out.println("bmax determined: " + bmax);
					bmax_p = bmax;
					lastRate = currentRate;
					//double the speed of the probe
					currentRate = currentRate * 2;
					currentFile++;
				}
			} catch (Exception e){
				e.printStackTrace();
			}

		}
	}

    public long[][] sendPackets(double probingSpeed) throws IOException {	
		InetAddress ip = InetAddress.getByName(ipAddress);

		FileOutputStream outputStream = new FileOutputStream("outputGenerator.txt");
	    PrintStream pout = new PrintStream(outputStream);

	    String currentLine = null;
	    DatagramPacket packet = null;
		

		DatagramSocket socket = new DatagramSocket();
		byte[] buf;
		
		long lastSendTime = 0;
	    long startTime = 0;
	    long currentTime = 0;
		long lastTimeStamp = 0;
		long baseTime = 0;
		long longTimestamp = 0;
		
	    StringTokenizer st;
		int packetsSent = 0;
		
		int seqNo;
		double timestamp;
		int packetSize;
		String type;

		double waitPeriod = ((double)(N * L * 8)/(probingSpeed * 1000));

		//cumulative departures in [][0], timestamp in [][1]
		long[][] departures = new long[N*TRAINS_IN_PROBE][2];
		int trainsDeparted = 0;

		//pout.println("wait period: " + waitPeriod + " N = " + N + " L = " + L + " r = " + r);
		try {
		    while(trainsDeparted < TRAINS_IN_PROBE) {  
			  	byte[][] packets = makePacketBattery(L, N, destPortNum, packetsSent);
			  	trainsDeparted++;

				for (int i = 0; i < packets.length; i++) {
					currentTime = System.nanoTime();
				    packet = new DatagramPacket(packets[i], packets[i].length, ip, boxPortNum);
				    socket.send(packet);

				    if (packetsSent == 0) {
				    	currentTime = baseTime = System.nanoTime();
				    	departures[0][0] = packets[i].length;
				    	departures[0][1] = (currentTime-baseTime)/1000;
				    }
				    else {
				    	departures[packetsSent][0] = departures[packetsSent-1][0] + packets[i].length;
				    	departures[packetsSent][1] = (currentTime-baseTime)/1000;
				    }

				   	pout.println(packetsSent + " " + (currentTime-baseTime)/1000  + " " + packets[i].length);
					packetsSent++;
				}

				//we take the time here to account for the runtime of our code
				lastSendTime = System.nanoTime();

				while(currentTime-lastSendTime < waitPeriod * 1000000000) {
				    currentTime = System.nanoTime();
				}	        
		    }
		}
		catch(IOException e) {
		    System.out.println(e.getMessage());
		}
		finally {
		    //signal receiver to close
		    byte[] fin = new byte[4];
		    System.arraycopy(toByteArray(destPortNum), 2, fin, 0, 2);
		    System.arraycopy(toByteArray(-1), 2, fin, 2, 2);
		    packet = new DatagramPacket(fin, fin.length, ip, boxPortNum);
		    
		    //send fin a few times to ensure that it is transmitted
		    for (int i = 0; i < 5; i++)
		    	socket.send(packet);

		    try {
			    outputStream.close();
			    pout.close();
			} catch (IOException e) {
			    System.out.println(e.getMessage());
			}
	    }
	    return departures;
    }

    public byte[][] makePacketBattery(int packetSize, int numPackets, int portNumber, int packetsSent) {
		//we know ahead of time we're not going to get more than 1024 * 1000 bytes in a single send.
		byte[][] packetBattery = new byte[numPackets][];
		for (int i = 0; i<numPackets; i++) {
			packetBattery[i] = new byte[packetSize];
			for(int j = 0; j < packetSize; j++) {
				packetBattery[i][j] = 'a';
			}
			System.arraycopy(toByteArray(destPortNum), 2, packetBattery[i], 0, 2);
			System.arraycopy(toByteArray(packetsSent + i), 2, packetBattery[i], 2, 2);
			//System.out.println("port number: " + fromByteArray(packetBattery[i], 0, 2));
		}
		return packetBattery;
	}

	/**
	* Converts an integer to a byte array.
	* @param value an integer
	* @return a byte array representing the integer
	*/
	public static byte[] toByteArray(int value) {
		byte[] Result = new byte[4];
		Result[3] = (byte) ((value >>> (8*0)) & 0xFF);
	 	Result[2] = (byte) ((value >>> (8*1)) & 0xFF);
		Result[1] = (byte) ((value >>> (8*2)) & 0xFF);
		Result[0] = (byte) ((value >>> (8*3)) & 0xFF);
		return Result;
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
