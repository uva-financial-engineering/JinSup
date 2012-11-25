import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    System.out.println("Enter the buy price (increments of 0.25): ");
    long buyPrice = (long) (scan.nextDouble() * 4);
    System.out.println("Enter the starting period in simulation seconds: ");
    long startTime = scan.nextLong() * 1000;
    System.out.println("Enter the end time in simulator seconds: ");
    long endTime = scan.nextLong() * 1000;

    MatchingEngine matchingEngine = new MatchingEngine(buyPrice);
    Controller controller = new Controller(startTime, endTime, matchingEngine);

    controller.runSimulator();

  }
}
