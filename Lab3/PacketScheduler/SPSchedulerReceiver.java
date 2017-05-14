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
			long hpdelay, lpdelay;
			int hpDropped = 0;
			int lpDropped = 0;
			
			//link capacity hard-coded for architecture reasons
			long lc = 20;
			// receive and process packets
			while (true)
			{
				byte[] buf = new byte[Buffer.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				// wait for packet, when arrives receive and recored arrival time
				socket.receive(packet);
				long startTime=System.nanoTime();
				
				//ORDER: time received, packet length, priority, high priority buffer size, low priority buffer size,
				//	 	 high priority packets dropped, low priority packets dropped, hp delay, lp delay
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
					//NOTE: this link capacity is hard-coded because of the architecture of the scheduler code.
					//delay = (bufferSize + packet.getLength()) * 8/20l;
				}
				pOut.print(hpDropped + "\t" + lpDropped + "\t");


				if (priority == (byte)2){
					hpdelay = (buffers[0].getSizeInBytes() + packet.getLength()) * 8l/lc;
					lpdelay = hpdelay + (buffers[1].getSizeInBytes() * 8l/lc);
				}
				else {
					hpdelay = buffers[0].getSizeInBytes() * 8l/lc;
					lpdelay = hpdelay + ((buffers[1].getSizeInBytes() + packet.getLength()) * 8l/lc);
				}
				pOut.print(hpdelay + "\t" + lpdelay);
				pOut.println();
				previsuTime = startTime;
				
				/*
				 * Process packet.
				 */
				// add packet to a queue if there is enough space
				if (priority == (byte)2) {
					if (buffers[0].addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
					{
						//System.err.println("High priority packet dropped.");
						hpDropped++;
					}
				}
				else if (priority == (byte)1) {
					if (buffers[1].addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
					{
						//System.err.println("Low priority packet dropped.");
						lpDropped++;
					}
				}
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
