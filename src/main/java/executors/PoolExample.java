package executors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

class MyTask implements Callable<String> {
  private static int nextId = 0;
  private int myId = nextId++;

  @Override
  public String call() throws Exception {
    System.out.println("Task " + myId + " starting on thread "
        + Thread.currentThread().getName());
    Thread.sleep((int) (Math.random() * 3000) + 1000);
    System.out.println("Task " + myId + " Ending!!");

    return "message from task " + myId;
  }
}

public class PoolExample {
  public static void main(String[] args) {
    int TASKS = 6;
    ExecutorService es = Executors.newFixedThreadPool(2);

    List<Future<String>> handles = new ArrayList<>();

    for (int i = 0; i < TASKS; i++) {
      handles.add(es.submit(new MyTask()));
    }
    System.out.println("All tasks submitted");

    // rejects future attempts to submit work
    // waits for all work to be completed, then kills all the worker threads
    es.shutdown();

    while (handles.size() > 0) {
      Iterator<Future<String>> ifs = handles.iterator();
      while (ifs.hasNext()) {
        Future<String> fs = ifs.next();
        if (fs.isDone()) {
          try {
            System.out.print("Task has completed, result is ");
            String rv = fs.get();
            System.out.println(rv);
          } catch (InterruptedException e) {
            System.out.println("foreground task shut request??");;
          } catch (ExecutionException e) {
            System.out.println("Task threw exception " + e.getCause());;
          }
          ifs.remove();
        }
      }
    }
    System.out.println("All tasks completed");
  }
}
