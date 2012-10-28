import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;


public class MatchingEngine {
	private HashMap<Agent, ArrayList<Order>> orderMap;
	
	//these priority queues will only have the 10 most recent orders
		private ArrayList<Order> allOrders;
//	private ArrayList<Order> priorityQueueBuy; //must keep sorted after each insert/removal
//	private ArrayList<Order> priorityQueueSell; 
	
	public MatchingEngine () {
		orderMap = new HashMap<Agent, ArrayList<Order>>();
//		priorityQueueBuy = new ArrayList<Order>();
//		priorityQueueSell = new ArrayList<Order>();
		allOrders = new ArrayList<Order>();
	}
	
	public void cancelOrder(Order o, Agent a){
		allOrders.remove(o);
		orderMap.get(a).remove(o);
		//log the action.
	}
	
	public void createOrder(Order o, Agent a) {
		allOrders.add(o);
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
	
	public double getLastTradePrice() {
		// TODO
		return 0.0;
	}
	
	
	// should verify again that this method should get the top ten, and
	// not the most recent orders in the orderbook.
	public ArrayList<Order> topBuyOrders() {
		ArrayList<Order> topBuyOrders = new ArrayList<Order>();
		int i = 0;
		while(i < allOrders.size() && !allOrders.get(i).isBuyOrder()) {
			i++;
		}
		
		// in case there are no buy orders in the array
		if(!allOrders.get(i).isBuyOrder()) {
			return topBuyOrders;
		} else {
			topBuyOrders.add(allOrders.get(i));
		}
		
		for(int j = i; j < allOrders.size(); j++) {
			if(allOrders.get(j).isBuyOrder()) {
				if(topBuyOrders.size() < 10)
					topBuyOrders.add(allOrders.get(j));
					Collections.sort(topBuyOrders);
			} else {
				if(allOrders.get(j).compareTo(topBuyOrders.get(9)) > 0){
					topBuyOrders.set(9, allOrders.get(j));
					Collections.sort(topBuyOrders);
				}
			}
		}
		return topBuyOrders;
	}
	
	public ArrayList<Order> topSellOrders() {
		ArrayList<Order> topSellOrders = new ArrayList<Order>();
		int i = 0;
		while(i < allOrders.size() && allOrders.get(i).isBuyOrder()) {
			i++;
		}
		
		// in case there are no buy orders in the array
		if(allOrders.get(i).isBuyOrder()) {
			return topSellOrders;
		} else {
			topSellOrders.add(allOrders.get(i));
		}
		
		for(int j = i; j < allOrders.size(); j++) {
			if(!allOrders.get(j).isBuyOrder()) {
				if(topSellOrders.size() < 10)
					topSellOrders.add(allOrders.get(j));
					Collections.sort(topSellOrders);
			} else {
				if(allOrders.get(j).compareTo(topSellOrders.get(9)) > 0){
					topSellOrders.set(9, allOrders.get(j));
					Collections.sort(topSellOrders);
				}
			}
		}
		return topSellOrders;
	}
	
}
