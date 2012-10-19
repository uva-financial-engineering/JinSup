
public class Agent {
	private long id;
	private MatchingEngine matchEng;
	
	public Agent(MatchingEngine matchEng) {
		id = System.currentTimeMillis();
		this.matchEng = matchEng;
	}

	public void Act() {
	}
	
	public boolean isActive(int time)
	{
		if(time >0)
			return true;
		else
			return false;
	}
	
	public void cancelOrder(Order o) {
		matchEng.cancelOrder(o, this);
	}
	
	public void createNewOrder(double price, int initialQuant, boolean buyOrder) {
		Order newOrder = new Order(this.id, price, initialQuant, buyOrder);
		matchEng.createOrder(newOrder, this);
	}
	
	public void modifyOrderPrice(Order o, double newPrice) {
		matchEng.modifyOrderPrice(o, this, newPrice);
	}
	
	public void modifyOrderQuant(Order o, int newQuant) {
		matchEng.modifyOrderQuant(o, this, newQuant);
	}
}