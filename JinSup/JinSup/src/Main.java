public class Main {

	public static Controller controller = new Controller();
	public static int CurrentTime = 0;
	public static void main(String[] args) {
		
		while (CurrentTime < 100) {
			
			for (Agent agent : controller.agentList)
			{
				if(agent.isActive(CurrentTime))
				{
					agent.Act();
				}
			}
			CurrentTime++;
		}
	}
}
