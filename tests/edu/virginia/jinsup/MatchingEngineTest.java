package edu.virginia.jinsup;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class MatchingEngineTest {

  private static final int TRADE_PRICE = 127000;
  private static final int TICK_SIZE = 25;
  private static final int SOME_AGENT_ID = -1;
  private MatchingEngine matchingEngine;
  private FundBuyer fundBuyer;
  private FundSeller fundSeller;

  @Before
  public void setUp() throws Exception {
    matchingEngine = new MatchingEngine(TRADE_PRICE, 0, true);
    fundBuyer = new FundBuyer(matchingEngine);
    fundSeller = new FundSeller(matchingEngine);
    matchingEngine.setStartingPeriod(false);
  }

  @Test
  public void willTradeTest_takeAll() {
    int orderedQuantities[] = new int[] {1, 2, 1};
    for (int i : orderedQuantities) {
      fundSeller.createNewOrder(TRADE_PRICE + TICK_SIZE, i, false);
    }
    Order buyOrder = new Order(SOME_AGENT_ID, TRADE_PRICE + TICK_SIZE, 4, true);
    ArrayList<Order> toTrade = matchingEngine.willTrade(buyOrder);
    ArrayList<Integer> resultList = new ArrayList<Integer>();

    for (Order order : toTrade) {
      resultList.add(order.getCurrentQuant());
      System.out.println(order.getId());
    }
    assertArrayEquals(orderedQuantities, arrayListToArray(resultList));
  }

  @Test
  public void willTradeTest_takeLess() {
    int orderedQuantities[] = new int[] {2, 2, 1,};
    int actualQuants[] = new int[] {2, 2};
    for (int i : orderedQuantities) {
      fundSeller.createNewOrder(TRADE_PRICE + TICK_SIZE, i, false);
    }
    Order buyOrder = new Order(SOME_AGENT_ID, TRADE_PRICE + TICK_SIZE, 4, true);
    ArrayList<Order> toTrade = matchingEngine.willTrade(buyOrder);
    ArrayList<Integer> resultList = new ArrayList<Integer>();

    for (Order order : toTrade) {
      resultList.add(order.getCurrentQuant());
      System.out.println(order.getId());
    }
    assertArrayEquals(actualQuants, arrayListToArray(resultList));
  }

  @Test
  public void willTradeTest_notEnough() {
    int orderedQuantities[] = new int[] {1, 2};
    for (int i : orderedQuantities) {
      fundSeller.createNewOrder(TRADE_PRICE + TICK_SIZE, i, false);
    }
    Order buyOrder = new Order(SOME_AGENT_ID, TRADE_PRICE + TICK_SIZE, 4, true);
    ArrayList<Order> toTrade = matchingEngine.willTrade(buyOrder);
    ArrayList<Integer> resultList = new ArrayList<Integer>();

    for (Order order : toTrade) {
      resultList.add(order.getCurrentQuant());
      System.out.println(order.getId());
    }
    assertArrayEquals(orderedQuantities, arrayListToArray(resultList));
  }

  @Test
  public void tradeLimitOrdersTest_leavesOrder() {
    tradeLimitOrderSetup();
    assertEquals(1, matchingEngine.getSellOrdersAsArrayList().size());
  }

  @Test
  public void tradeLimitOrderTest_leavesQuant() {
    tradeLimitOrderSetup();
    assertEquals(1, matchingEngine.getSellOrdersAsArrayList().get(0)
      .getCurrentQuant());
  }

  @Test
  public void tradeLimitOrderTest_mutualDepletion() {
    tradeLimitOrderSetup();
    fundBuyer.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, 1, true);
    assertEquals(0, matchingEngine.getSellOrdersAsArrayList().size());
  }

  @Test
  public void tradeLimitOrderTest_shortageSellSide() {
    tradeLimitOrderSetup();
    fundBuyer.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, 2, true);
    assertEquals(0, matchingEngine.getSellOrdersAsArrayList().size());
  }

  @Test
  public void tradeLimitOrderTest_shortageBuySide() {
    tradeLimitOrderSetup();
    fundBuyer.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, 2, true);
    assertEquals(1, matchingEngine.getBuyOrdersAsArrayList().size());
  }

  @Test
  public void tradeLimitOrderTest_shortageBuySideLeavesQuant() {
    tradeLimitOrderSetup();
    fundBuyer.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, 2, true);
    assertEquals(1, matchingEngine.getBuyOrdersAsArrayList().get(0)
      .getCurrentQuant());
  }

  public void tradeLimitOrderSetup() {
    int orderedQuantities[] = new int[] {1, 2};
    for (int i : orderedQuantities) {
      fundSeller.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, i, false);
    }
    fundBuyer.createNewOrder(TRADE_PRICE + TICK_SIZE * 2, 2, true);
  }

  public int[] arrayListToArray(ArrayList<Integer> arrayList) {
    int result[] = new int[arrayList.size()];
    for (int i = 0; i < arrayList.size(); ++i) {
      result[i] = arrayList.get(i);
    }
    return result;
  }
}
