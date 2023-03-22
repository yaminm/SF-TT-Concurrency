package stopper;

class MyTask implements Runnable {
  private volatile boolean stop = false;

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
    + " Starting");


    while (! stop)
      // print will almost certainly "break" this code
      // in the sense that it will stop as expected!
      /*System.out.println(".")*/;

    System.out.println(Thread.currentThread().getName()
    + " Stopping");
  }

  public void stopRequest() {
    stop = true;
    System.out.println("stop is " + stop);
  }
}
public class Stopper {
  public static void main(String[] args) throws InterruptedException {
    MyTask mt = new MyTask();
    Thread t1 = new Thread(mt);
    t1.start();
    System.out.println("main launched worker...");
    Thread.sleep(1_000);
    System.out.println("main calling stopRequest()");
    mt.stopRequest();
    System.out.println("run completed");
  }
}
