import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


public class MatchingEngine {
	private HashMap<Agent, ArrayList<Order>> orderMap;
	private TreeSet<Order> priorityQueue; //must keep sorted after each insert/removal
	
	public MatchingEngine () {
		orderMap = new HashMap<Agent, ArrayList<Order>>();
		priorityQueue = new TreeSet<Order>();
	}
	
	public void cancelOrder(Order o, Agent a){
		priorityQueue.remove(o);
		orderMap.get(a).remove(o);
		//log the action.
	}
	
	public void createOrder(Order o, Agent a) {
		priorityQueue.add(o);
		if(orderMap.containsKey(a)) {
			orderMap.get(a).add(o);
		}
		else {
			ArrayList<Order> orderList = new ArrayList<Order>();
			orderList.add(o);
			orderMap.put(a, orderList);
		}
		//log the action.
	}
	
	public void modifyOrder(Order o, double newPrice, int newQuant) {
		o.setPrice(newPrice);
		o.setQuant(newQuant);
		//log the action
	}
	
}
