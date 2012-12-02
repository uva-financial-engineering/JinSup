import java.util.Scanner;

import com.beust.jcommander.JCommander;

public class Main {

  public static void main(String[] args) {
    Settings settings = new Settings();
    new JCommander(settings, args);
    System.out.println(settings.getBuyPrice());

    Scanner scan = new Scanner(System.in);
    System.out.println("Enter the buy price in dollars (increments of 0.25): ");
    int buyPrice = (int) (scan.nextDouble() * 100);
    System.out.println("Enter the starting period in simulation seconds: ");
    int startTime = scan.nextInt() * 1000;
    System.out.println("Enter the end time in simulation seconds: ");
    int endTime = scan.nextInt() * 1000;
    scan.close();

    MatchingEngine matchingEngine = new MatchingEngine(buyPrice);
    Controller controller = new Controller(startTime, endTime, matchingEngine);

    System.out.println("Starting simulator...");
    controller.runSimulator();
  }
}