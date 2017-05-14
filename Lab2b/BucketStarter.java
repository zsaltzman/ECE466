import TokenBucket.TokenBucket;

public class BucketStarter{

public static void main(String[] args) {
	// listen on port 4444, send to localhost:4445,
	 // max. size of received packet is 1024 bytes,
	// buffer capacity is 100*1024 bytes,
	// token bucket has 10000 tokens, rate 5000 tokens/sec, and
	// records packet arrivals to bucket.txt).

	//PORT RECEIVER, IP ADDRESS, PORT SENDER, MAX PACKET SIZE, BUFFER CAPACITY, MAX TOKENS, TOKEN RATE
	 TokenBucket lb = new TokenBucket(4444, "localhost", 4445,
	 100, 6500000 / 8, 100, (700000 /8), "bucket.txt");
	 new Thread(lb).start();
	 }
}