import TokenBucket.TokenBucket;

public class BucketStarter {
 
public static void main(String[] args) {
// listen on port 4444, send to localhost:4445,
 // max. size of received packet is 1024 bytes,
// buffer capacity is 100*1024 bytes,
// token bucket has 10000 tokens, rate 5000 tokens/sec, and
// records packet arrivals to bucket.txt).
 TokenBucket lb = new TokenBucket(4444, "localhost", 4445,
 1024, 100*1024, 10000, 5000, "bucket.txt");
 new Thread(lb).start();
 }
}
