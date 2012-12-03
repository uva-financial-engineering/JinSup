import com.beust.jcommander.JCommander;

public class JinSup {

  public static Settings settings;

  public static void main(String[] args) {
    settings = new Settings();
    new JCommander(settings, args);
    int buyPrice;
    int startTime;
    int endTime;
    if (settings.isSet()) {
      Controller.graphFrame = new GraphFrame(true);
      buyPrice = (int) settings.getBuyPrice().doubleValue() * 100;
      startTime = settings.getStartTime() * 1000;
      endTime = startTime + settings.getTradeTime() * 1000;
    } else {
      Controller.graphFrame = new GraphFrame(false);
      buyPrice = Controller.graphFrame.getBuyPrice();
      startTime = Controller.graphFrame.getStartTime();
      endTime = Controller.graphFrame.getEndTime();
    }
    MatchingEngine matchingEngine = new MatchingEngine(buyPrice);
    Controller controller = new Controller(startTime, endTime, matchingEngine);
    System.out.println("Starting simulator...");
    controller.runSimulator();
  }
}