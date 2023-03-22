package runnables;

class MyTask implements Runnable {
  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
        + " My Task starting...");
    for (int i = 0; i < 100; i++) {
      System.out.println(Thread.currentThread().getName()
          + " i is " + i);
      // if interrupted, blocking methods throw InterruptedException
      if (Thread.interrupted()) {
        System.out.println("oops, shutdown requested");
        break;
      }
    }

    System.out.println(Thread.currentThread().getName()
        + " My Task ending...");
  }
}

class HoldItAlive implements Runnable {

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
        + " starting HoldItAlive");
    while(true)
      ;
  }
}
public class SimpleRunnable {
  public static void main(String[] args) throws InterruptedException {
    MyTask mt = new MyTask();
    System.out.println(Thread.currentThread().getName()
        + " main method about to start thread");
//    mt.run();
    Thread t1 = new Thread(mt);
    // JVM exits when there are ZERO non-daemon threads alive
    t1.setDaemon(true); // usually not a good idea...
    t1.start();

    // another NON-DAEMON THREAD
//    new Thread(new HoldItAlive()).start();
//    Thread.sleep(1);
//    t1.interrupt();
    System.out.println(Thread.currentThread().getName()
        + " main method about to end");
  }
}
