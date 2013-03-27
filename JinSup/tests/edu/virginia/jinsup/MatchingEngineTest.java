package edu.virginia.jinsup;
import static org.junit.Assert.assertEquals;

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

    Order o1 = new Order(1, 1000, 1, true);
    Order o2 = new Order(1, 50, 3, true);

    orders.add(o2);
    orders.add(o1);

    Order o3 = new Order(1, 1000, 1, true);

    for (int i = 0; i < 1000000000; i++) {
      // stall the system
    }

    Order o4 = new Order(2, 1000, 1, true);

    ordersTime.add(o4);
    ordersTime.add(o3);

    matchingEngine = new MatchingEngine(10000);
    buyer = new FundBuyer(matchingEngine);
    seller = new FundSeller(matchingEngine);
    buyer.createNewOrder(20, 40, true);
    buyer.createNewOrder(10000, 40, true);
    buyer.createNewOrder(21, 40, true);
    buyer.createNewOrder(26, 40, true);
    buyer.createNewOrder(23, 40, true);
    buyer.createNewOrder(22, 40, true);
    buyer.createNewOrder(29, 40, true);
    buyer.createNewOrder(27, 40, true);
    buyer.createNewOrder(28, 40, true);
    buyer.createNewOrder(24, 40, true);
    buyer.createNewOrder(25, 40, true);

    seller.createNewOrder(20000, 50, false);
    seller.createNewOrder(2, 3, false);
    seller.createNewOrder(1, 2, false);
    seller.createNewOrder(3, 2, false);
    seller.createNewOrder(4, 2, false);
    seller.createNewOrder(10, 2, false);
    seller.createNewOrder(5, 2, false);
    seller.createNewOrder(6, 2, false);
    seller.createNewOrder(7, 2, false);
    seller.createNewOrder(9, 2, false);
    seller.createNewOrder(8, 2, false);

  }

  // @Test
  // public void testCheckMakeTrade() {
  // seller.createNewOrder(100.00, 50, false);
  // System.out.println(matchingEngine.getAllOrders().get(0).getCurrentQuant());
  // assertEquals(10,matchingEngine.getAllOrders().get(0).getCurrentQuant());
  // }

  @Test
  public void testComparatorsHigestPrice() {
    Collections.sort(orders, Order.highestFirstComparator);
    assertEquals((double) 1000, (double) orders.get(0).getPrice(), 0.0001);
  }

  @Test
  public void testComparatorsLowestPrice() {
    Collections.sort(orders, Order.lowestFirstComparator);
    assertEquals((double) 50, (double) orders.get(0).getPrice(), 0.0001);
  }

  @Test
  public void testComparatorsHighestTime() {
    Collections.sort(ordersTime, Order.highestFirstComparator);
    assertEquals((double) 1, (double) orders.get(0).getCreatorID(), 0.0001);
  }

  @Test
  public void testComparatorsLowestTime() {
    Collections.sort(ordersTime, Order.lowestFirstComparator);
    assertEquals((double) 1, (double) orders.get(0).getCreatorID(), 0.0001);
  }

  @Test
  public void testgetBestBid() {
    assertEquals((double) 10000, (double) matchingEngine.getBestBid()
      .getPrice(), 0.0001);
  }

  @Test
  public void testgetBestAsk() {
    assertEquals((double) 1, (double) matchingEngine.getBestAsk().getPrice(),
      0.0001);
  }

  @Test
  public void testTopBuyOrders() {
    ArrayList<Order> buyOrders = matchingEngine.topBuyOrders();
    for (int i = 0; i < 10; i++) {
      System.out.println(buyOrders.get(i).getPrice());
    }
  }

  @Test
  public void testTopSellOrders() {
    ArrayList<Order> sellOrders = matchingEngine.topSellOrders();
    for (int i = 0; i < 10; i++) {
      System.out.println(sellOrders.get(i).getPrice());
    }
  }

  @Test
  public void testCreateMarketOrder() {
    buyer.createMarketOrder(5, true);
    assertEquals((double) 3, (double) matchingEngine.topSellOrders().get(0)
      .getPrice(), 0.0001);
  }
}
