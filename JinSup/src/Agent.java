
public class Agent {
	private long id;
	private MatchingEngine matchEng;
	private long nextActTime;
	private boolean willAct;
	private int inventory; //number of shares bought or sold. -ve if only sold, +ve if only bought
	
	public Agent(MatchingEngine matchEng) {
		id = System.currentTimeMillis();
		this.matchEng = matchEng;
		nextActTime = -1;
	}

	public void act() {
		
	}
	
//	public boolean isActive(int time)
//	{
//		if(time >0)
//			return true;
//		else
//			return false;
//	}
//	
	
	public long getNextActTime() {
		return nextActTime;
	}
	
	public boolean getWillAct() {
		return willAct;
	}
	
	public void setWillAct(boolean act) {
		willAct = act;
	}
	
	
	public void cancelOrder(Order o) {
		matchEng.cancelOrder(o, this);
	}
	
	public void createNewOrder(double price, int initialQuant, boolean buyOrder) {
		Order newOrder = new Order(this.id, price, initialQuant, buyOrder);
		matchEng.createOrder(newOrder, this);
	}
	
	public void modifyOrder(Order o, double newPrice, int newQuant) {
		matchEng.modifyOrder(o, newPrice, newQuant);
	}
	
}