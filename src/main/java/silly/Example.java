package silly;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class Ex2 {
  public static void main(String[] args) {
    System.out.println("This is another main method");
    System.out.println("is this another 'program'???");
  }
}

public class Example {
  // 1 JVM invocation will start ONE main method
  public static void main(String[] args) throws Throwable{
    System.out.println("entering Example.main");
//    Example.main(null);
    // but beyond that "main" is just a method nothing more
    Ex2.main(null);
    Ex2.main(null);
    Ex2.main(null);
    System.out.println("Start a new OS process");
    Process rv = Runtime.getRuntime().exec("ls -l /home/simon");
    BufferedReader br = new BufferedReader(new InputStreamReader(rv.getInputStream()));
    String line = null;
    while ((line = br.readLine()) != null) {
      System.out.println(">>> " + line);
    }
    System.out.println("back from the OS");
  }
}
