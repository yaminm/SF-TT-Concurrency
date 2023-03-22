package runnables;

class CountWorker implements Runnable {
  private /*volatile*/ long count = 0;
  // avoid synchronizing on "this"
  // - there will be public references to that!
  private Object rendezvous = new Object();
  @Override
  public void run() {
    for (long i = 0; i < 100_000_000; i++) {
      synchronized (this.rendezvous) {
        count++;
      }
    }
  }
  public long getCount() {
    return count;
  }
}

public class Counter2 {
  public static void main(String[] args) throws InterruptedException {
    CountWorker cw = new CountWorker();
    Thread t1 = new Thread(cw);
    t1.start();
    Thread t2 = new Thread(cw);
    t2.start();

    long start = System.nanoTime();

//    Thread.sleep(1_000); // yuk
    t1.join(); // waits until t1 has died
    t2.join();

    long elapsed = System.nanoTime() - start;
    System.out.println("count value is " + cw.getCount());
    System.out.printf("time taken: %7.3f\n", (elapsed / 1_000_000_000.0) );
  }
}
