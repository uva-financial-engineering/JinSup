import java.util.ArrayList;

public class Controller {
  public ArrayList<Agent> agentList;

  public Controller() {
    agentList = new ArrayList<Agent>();
  }

  public int createAgents(int num) {
    for (int i = 0; i < num; i++) {
      agentList.add(new Agent(hi));
    }
    return agentList.size();
  }
}
