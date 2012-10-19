import java.util.Comparator;


public class Order implements Comparator<Order>{
	private double price;
	private int originalQuant;
	private int currentQuant;
	private long id;
	private boolean buyOrder; //true if this is a buy order.
	private long agentId; //id of the agent that initiated the order

	public Order(long agentId, double price, int originalQuant, boolean buyOrder) {
		id = System.currentTimeMillis();
		this.agentId = agentId;
		this.buyOrder = buyOrder;
		this.originalQuant  = originalQuant;
		this.price = price;
		currentQuant = originalQuant;
	}

	public long getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	public int getOriginalQuant() {
		return originalQuant;
	}

	public int getCurrentQuant() {
		return currentQuant;
	}

	public void setQuant(int newQuant) {
		currentQuant = newQuant;
	}

	public void setPrice(double newPrice) {
		price = newPrice;
	}

	@Override
	public int compare(Order arg0, Order arg1) {
		// TODO Auto-generated method stub
		//check this double comparison!!
		//multiply by 100 and cast price as int.
		// object is "less than" (higher on the queue) if price is greater, ms is smaller.
		if((int)(arg0.price*100) - (int)(arg1.price*100) != 0) {
			return -1*((int)(arg0.price*100) - (int)(arg1.price*100));
		}
		else {
			return (int) (arg0.id/100 - arg1.id/100);
		}
	}



}
