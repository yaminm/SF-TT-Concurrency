package badqueue;

public class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])new Object[CAPACITY];
  private int count = 0;

  public void put(E e) {
    // oops, what if this were already full!
    data[count++] = e;
  }

  public E take() {
    // from the front of the queue
    // hmm, what if it's empty...

    E rv = data[0];
    System.arraycopy(data, 1, data, 0, --count);
    return rv;
  }


}
