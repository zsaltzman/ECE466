package Estimator;

public class Estimator implements Runnable {
	private TrafficGenerator generator;
	private TrafficSink sink;

	public static void main(String[] args) {
		//port generator sends to
		int generatorPort;

		//ip generator sends to
		String generatorIp;

		//port sink listens on
		int sinkPort;

		//parameters of packet train
		int N, L;
		int r;
		if (args.length < 6) {
			generatorPort = 4445;
			sinkPort = 4445;
			generatorIp = "localhost";
			N = 1;
			L = 1;
			r = 1;
		}
		else {
			generatorPort = Integer.parseInt(args[0]);
			generatorIp = args[2];
			sinkPort = Integer.parseInt(args[1]);
			N = Integer.parseInt(args[3]);
			L = Integer.parseInt(args[4]);
			r = Integer.parseInt(args[5]);
		}

		new Thread(new Estimator(generatorPort, sinkPort, generatorIp, N, L, r)).start();
	}

	public Estimator(int _generatorPort, int _sinkPort, String _generatorIp, int _N, int _L, int _r) {
		generator = new TrafficGenerator(_generatorPort, _sinkPort, _generatorIp, _N, _L, _r);
		sink = new TrafficSink(_sinkPort);
	}

	public void run() {
		new Thread(sink).start();
		new Thread(generator).start();
	}
}

	/*int portNum;
    	String ipAddress;
		if (args.length < 2) {
			portNum = 4445;
			ipAddress = "localhost";
		}
		else {
			portNum = Integer.parseInt(args[0]);
			ipAddress = args[1];
		}*/