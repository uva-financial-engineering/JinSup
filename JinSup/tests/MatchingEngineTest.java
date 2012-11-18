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
	private ArrayList<Order> ordersTime = new ArrayList<Order>();

	@Before
	public void setUp() throws Exception {
//		matchingEngine = new MatchingEngine();
//		buyer = new Agent(matchingEngine);
//		seller = new Agent(matchingEngine);
//		buyer.createNewOrder(100.00, 40, true);
	  
	
	Order o1 = new Order(1, 1000, 1, true);
	Order o2 = new Order(1, 50, 3, true);
	
	orders.add(o2);
	orders.add(o1);
	
	Order o3 = new Order(1, 1000, 1, true);
	
	for(int i = 0; i < 1000000000; i++) {
	  // stall the system
	}
	
	Order o4 = new Order(2, 1000, 1, true);
	
	ordersTime.add(o4);
	ordersTime.add(o3);
		
	}


//	@Test
//	public void testCheckMakeTrade() {
//		seller.createNewOrder(100.00, 50, false);
//		System.out.println(matchingEngine.getAllOrders().get(0).getCurrentQuant());
//		assertEquals(10,matchingEngine.getAllOrders().get(0).getCurrentQuant());
//	}
	
	@Test
	public void testComparatorsHigestPrice() {
	  Collections.sort(orders,Order.highestFirstComparator);
	  assertEquals((double)1000,(double)orders.get(0).getPrice(), 0.0001);
	}
	
	@Test
    public void testComparatorsLowestPrice() {
      Collections.sort(orders,Order.lowestFirstComparator);
      assertEquals((double)50,(double)orders.get(0).getPrice(), 0.0001);
    }
	
	@Test
    public void testComparatorsHighestTime() {
      Collections.sort(ordersTime,Order.highestFirstComparator);
      assertEquals((double)1,(double)orders.get(0).getCreatorID(), 0.0001);
    }
	
	public void testComparatorsLowestTime() {
      Collections.sort(ordersTime,Order.lowestFirstComparator);
      assertEquals((double)1,(double)orders.get(0).getCreatorID(), 0.0001);
    }
	
	
	

}
