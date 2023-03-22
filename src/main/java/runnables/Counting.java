package runnables;

class Counter implements Runnable {
  int i = 0;
  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
        + " starting counter task");
    for (; i < 1_000; i++) {
      System.out.println(Thread.currentThread().getName()
          + " i is " + i);
    }
    System.out.println(Thread.currentThread().getName()
        + " ending counter task");
  }
}

public class Counting {
  public static void main(String[] args) {
    Counter c = new Counter();
    Thread t1 = new Thread(c);
    Thread t2 = new Thread(c);
    t1.start();
    t2.start();
    System.out.println("main exiting.");
  }
}
