package badqueue;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])new Object[CAPACITY];
  private int count = 0;
//  private Object rendezvous = new Object();
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition NOT_EMPTY = lock.newCondition();
  private final Condition NOT_FULL = lock.newCondition();

  public void put(E e) throws InterruptedException {
// code NEVER continues execution UNLESS it gains the lock
//    synchronized (this.rendezvous) {

    lock.lock(); // variations that permit timeouts, or interrupts
    try {
      // oops, what if this were already full!
      // beware of "lock spining" or "busy waiting" ...
      // the continuous testing of the condition
      while (count == CAPACITY) { // MUST re-test the condition
        // temporary release of lock needed!
//        Thread.sleep(1); // NOPE, sleep does NOT release the lock
        // wait "stops execution" until...
        // and releases the lock as it does so
        // ALSO RECLAIMS the lock before continuing
//        this.rendezvous.wait();
        NOT_FULL.await();
      }
      data[count++] = e;
//      this.rendezvous.notify();
//      this.rendezvous.notifyAll();
      NOT_EMPTY.signal();
    } finally {
      lock.unlock();
    }
  }

  public E take() throws InterruptedException {
//    synchronized (this.rendezvous) {
    lock.lock();
    try {
      // from the front of the queue
      // hmm, what if it's empty...
      while (count == 0)
      {
        // temporary release of lock needed!
//        this.rendezvous.wait();
        NOT_EMPTY.await();
      }

      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
//      this.rendezvous.notify();
//      this.rendezvous.notifyAll(); // "correct" but not scalable :(
      NOT_FULL.signal();
      return rv;
    } finally {
      lock.unlock();
    }
  }
}

// Producer-Consumer model

// Pipeline architecture -- often describing network-based queue

// Actor framework -- typically "messages" are instructions with data
// and data structures passed over the queue are typically immutable
// WARNING "immutable data" does NOT exist,
// because initialization IS MUTATION -- this is the visibility problem

class TryTheQueue {
  public static void main(String[] args) throws InterruptedException {
    final BadQueue<int[]> queue = new BadQueue<>();
//    final BlockingQueue<int[]> queue = new ArrayBlockingQueue<>(10);
    final int COUNT = 10_000_000;

    Runnable producer = () -> {
      System.out.println("Producer starting");
      try {
        for (int i = 0; i < COUNT; i++) {
          // MUST make brand new, self-contained object
          // EACH TIME ROUND
          int[] data = {i, 0}; // transactionally bad, should match!
//          if (i < 500) {
//            // allow more visible transactional wrong-ness
//            // and exercise empty queue
//            Thread.sleep(1);
//          }
          data[1] = i; // transactionally sound data!!!
          queue.put(data); // NOW it's shared!!!
          data = null; // protect against accidental usage
        }
      } catch (InterruptedException ie) {
        System.out.println("Surprising??? someone asked us to shutdown");
      }
      System.out.println("Producer finishing");
    };
    Runnable consumer = () -> {
      System.out.println("Consumer starting");
      try {
        for (int i = 0; i < COUNT; i++) {
          int [] data = queue.take();
//          if (i > 9_500) {
//            Thread.sleep(1);
//          }
          if (data[0] != data[1] || data[0] != i) {
            System.out.println("**** ERROR at index " + i
                + ": " + Arrays.toString(data));
          }
        }
      } catch (InterruptedException ie) {
        System.out.println("Odd, interrupt in consumer");
      }
      System.out.println("Consumer ending");
    };
    long start = System.nanoTime();

    Thread prod = new Thread(producer);
    prod.start();
    Thread cons = new Thread(consumer);
    cons.start();
    System.out.println("Workers started");
    prod.join();
    cons.join();

    long elapsed = System.nanoTime() - start;
    System.out.println("Workers finished, shutting down");
    System.out.printf("Time taken: %7.3f\n", elapsed/1_000_000_000.0);
  }
}

/*
synchronized/wait/notifyAll ->  3.5 ~4 ish

The below two are likely more scalable...
BlockingQueue ->  3.5 ~4 ish
ReentrantLock -> same again :)

 */