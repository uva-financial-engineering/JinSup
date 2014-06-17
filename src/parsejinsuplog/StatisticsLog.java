/*
 * This is the main class to analyze the log file from JinSup simulator.
 * First we call ParseLogFile to parse the log. Next, we implement different
 * methods to obtain statistics from the ArrayList of orders.
 */
package parsejinsuplog;

/**
 * 
 * @author enriqueareyan
 */
public class StatisticsLog {
  /**
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    /* Initialize variables */
    int i = 0;
    String logFileLocation, RFileLocation;
    ParseLogFile R;
    SimulationLog S;
    System.out.println("Start....");
    /* Log batch processing */
    // for(i = 1; i<10;i++){
    logFileLocation = "data/log" + i + ".csv";
    System.out.println("Processing log file number " + i + ", located at: "
      + logFileLocation);
    /* Parse Log File */
    R = new ParseLogFile(logFileLocation);
    /* Feed the simulation with the parse data in the form of a map of agents */
    S = new SimulationLog(R.getAgents(), R.getOrderBook());
    S.printOrderBook();
    // S.printSimulation();
    RFileLocation = "data/summary" + i + ".R";
    S.createRFile(RFileLocation);
    // }
  }
}
