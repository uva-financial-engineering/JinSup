import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    System.out.println("Enter the buy price in dollars (increments of 0.25): ");
    long buyPrice = (long) (scan.nextDouble() * 100);
    System.out.println("Enter the starting period in simulation seconds: ");
    long startTime = scan.nextLong() * 1000;
    System.out.println("Enter the end time in simulation seconds: ");
    long endTime = scan.nextLong() * 1000;
    scan.close();

    MatchingEngine matchingEngine = new MatchingEngine(buyPrice);
    Controller controller = new Controller(startTime, endTime, matchingEngine);

    controller.runSimulator();
  }
}
