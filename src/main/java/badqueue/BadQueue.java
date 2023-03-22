package badqueue;

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
