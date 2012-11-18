import java.util.ArrayList;

public class Controller {
  public ArrayList<Agent> agentList;
  private long time;

  public Controller()
  {
    agentList = new ArrayList<Agent>();
    time = 0;
  }

  public int createAgents(int num)
  {
    for(int i = 0; i < num; i++)
    {
//      agentList.add(new Agent());
    }
    return agentList.size();
    //this is a test!
  }


  public void selectActingAgent() {
    ArrayList<Agent> actingAgents = new ArrayList<Agent>();
    for(int i = 0; i < agentList.size(); i++) {
      if(agentList.get(i).getNextActTime() == time) {
        actingAgents.add(agentList.get(i));
      }
    }
    //pick a random agent to activate
    while(!actingAgents.isEmpty()) {
      int agentIndex = (int)(Math.random()*((actingAgents.size()-1) + 1));
      Agent acting = actingAgents.get(agentIndex);
      acting.setWillAct(true);
      activateAgent(acting);
      actingAgents.remove(agentIndex);
    }
    time += 1;
  }

  //run the agent until it does not have any more actions to do.
  public void activateAgent(Agent a) {
    while(a.getWillAct()) {
      a.act();
    }
        //also log the action
  }


}

