package PacketScheduler;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Listens on specified port for incoming packets.
 * Packets are stored to queues.
 */
public class SchedulerReceiver implements Runnable
{
	// queues used to store incoming packets 
	private Buffer[] buffers;
	// port on which packets are received
	private int port;
	// name of output file
	private String fileName;
	
	/**
	 * Constructor.
	 * @param buffers Buffers to which packets are stored. 
	 * @param port Port on which to lister for packets.
	 * @param fileName Name of output file.
	 */
	public SchedulerReceiver(Buffer[] buffers, int port, String fileName)
	{
		this.buffers = buffers;
		this.port = port;
		this.fileName = fileName;
	}

	/**
	 * Listen on port and send out or store incoming packets to buffers.
	 * This method is invoked when starting a thread for this class.
	 */  
	public void run()
	{		
		DatagramSocket socket = null;
		PrintStream pOut = null;	
		byte priority;
		try
		{
			FileOutputStream fOut =  new FileOutputStream(fileName);
			pOut = new PrintStream (fOut);
			long previsuTime = 0;
			
			socket = new DatagramSocket(port);
			long f1delay, f2delay, f3delay;
			int f1Dropped = 0;
			int f2Dropped = 0;
			int f3Dropped = 0;

			//link capacity and flow weights hard-coded for architecture reasons
			long lc = 10;
			int w1 = 3;
			int w2 = 1;
			int w3 = 1;
			int wtotal = w1+w2+w3;

			// receive and process packets
			while (true)
			{
				byte[] buf = new byte[Buffer.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				// wait for packet, when arrives receive and recored arrival time
				socket.receive(packet);
				long startTime=System.nanoTime();
				
				//ORDER: time received, packet length, priority, f1 buffer size, f2 buffer size, f3 buffer size
				//	 	 f1 packets dropped, f2 packets dropped, f3 packets dropped, f1 delay, f2 delay, f3 delay
				//		
				/*
				 * Record arrival to file in following format:
				 * elapsed time (microseconds), packet size (bytes), backlog in buffers ordered by index in array (bytes).
				 */
				// to put zero for elapsed time in first line
				if(previsuTime == 0)
				{
					previsuTime = startTime;
				}
				priority = packet.getData()[0];
				pOut.print((startTime-previsuTime)/1000 + "\t" + packet.getLength() + "\t" + priority + "\t");
				for (int i = 0; i<buffers.length; i++)
				{
					long bufferSize = buffers[i].getSizeInBytes();
					pOut.print(bufferSize + "\t");
				}
				pOut.print(f1Dropped + "\t" + f2Dropped + "\t" + f3Dropped + "\t");
				
				previsuTime = startTime;
				
				/*
				 * Process packet.
				 */
				// add packet to a queue if there is enough space
				if (priority == (byte)1) {
					if (buffers[0].addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
					{
						//System.err.println("High priority packet dropped.");
						f1Dropped++;
					}		
				}
				else if (priority == (byte)2) {
					if (buffers[1].addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
					{
						//System.err.println("Low priority packet dropped.");
						f2Dropped++;
					}
				}
				else if (priority == (byte)3) {
					if (buffers[2].addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
					{
						//System.err.println("Low priority packet dropped.");
						f3Dropped++;
					}
				}
				f1delay = buffers[0].getSizeInBytes() * 8l/lc * w1/wtotal;
				f2delay = buffers[1].getSizeInBytes() * 8l/lc * w2/wtotal;
				f3delay = buffers[2].getSizeInBytes() * 8l/lc * w3/wtotal;

				pOut.print(f1delay + "\t" + f2delay + "\t" + f3delay);
				pOut.println();

				/*
				 * TODO: 
				 * Replace previous command with code that:
				 * - implements packet classifier 
				 * - stores packets to appropriate queue
				 * - reports and/or logs packets drops with all information you need
				 */
			}
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
