import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class MatchingEngineTest {

	private Agent buyer;
	private Agent seller;
	private MatchingEngine matchingEngine;
	private ArrayList<Order> orders = new ArrayList<Order>();

	@Before
	public void setUp() throws Exception {
//		matchingEngine = new MatchingEngine();
//		buyer = new Agent(matchingEngine);
//		seller = new Agent(matchingEngine);
//		buyer.createNewOrder(100.00, 40, true);
	  
	
	Order o1 = new Order(1, 100.00, 1, true);
	Order o2 = new Order(1, 50, 3, true);
	
	orders.add(o2);
	orders.add(o1);
		
	}


//	@Test
//	public void testCheckMakeTrade() {
//		seller.createNewOrder(100.00, 50, false);
//		System.out.println(matchingEngine.getAllOrders().get(0).getCurrentQuant());
//		assertEquals(10,matchingEngine.getAllOrders().get(0).getCurrentQuant());
//	}
	
	@Test
	public void testComparators() {
	  Collections.sort(orders,null);
	  assertEquals(100.00,orders.get(0).getPrice());
	}

}
