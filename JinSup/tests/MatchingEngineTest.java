import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MatchingEngineTest {

	private Agent buyer;
	private Agent seller;
	private MatchingEngine matchingEngine;

	@Before
	public void setUp() throws Exception {
		matchingEngine = new MatchingEngine();
		buyer = new Agent(matchingEngine);
		seller = new Agent(matchingEngine);
		buyer.createNewOrder(100.00, 40, true);
		
		
	}


	@Test
	public void testCheckMakeTrade() {
		seller.createNewOrder(100.00, 50, false);
		System.out.println(matchingEngine.getAllOrders().get(0).getCurrentQuant());
		assertEquals(10,matchingEngine.getAllOrders().get(0).getCurrentQuant());
	}

}
