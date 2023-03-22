package badqueue;

import java.util.Arrays;

public class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])new Object[CAPACITY];
  private int count = 0;
  private Object rendezvous = new Object();

  public void put(E e) throws InterruptedException {
    synchronized (this.rendezvous) {
      // oops, what if this were already full!
      // beware of "lock spining" or "busy waiting" ...
      // the continuous testing of the condition
      while (count == CAPACITY) { // MUST re-test the condition
        // temporary release of lock needed!
//        Thread.sleep(1); // NOPE, sleep does NOT release the lock
        // wait "stops execution" until...
        // and releases the lock as it does so
        // ALSO RECLAIMS the lock before continuing
        this.rendezvous.wait();
      }
      data[count++] = e;
      this.rendezvous.notify();
    }
  }

  public E take() throws InterruptedException {
    synchronized (this.rendezvous) {
      // from the front of the queue
      // hmm, what if it's empty...
      while (count == 0)
      {
        // temporary release of lock needed!
        this.rendezvous.wait();
      }

      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      this.rendezvous.notify();
      return rv;
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
    final int COUNT = 10_000;

    Runnable producer = () -> {
      System.out.println("Producer starting");
      try {
        for (int i = 0; i < COUNT; i++) {
          // MUST make brand new, self-contained object
          // EACH TIME ROUND
          int[] data = {i, 0}; // transactionally bad, should match!
          if (i < 500) {
            // allow more visible transactional wrong-ness
            // and exercise empty queue
            Thread.sleep(1);
          }
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
          if (i > 9_500) {
            Thread.sleep(1);
          }
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
    Thread prod = new Thread(producer);
    prod.start();
    Thread cons = new Thread(consumer);
    cons.start();
    System.out.println("Workers started");
    prod.join();
    cons.join();
    System.out.println("Workers finished, shutting down");
  }
}

