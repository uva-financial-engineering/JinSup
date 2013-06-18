package edu.virginia.jinsup;

import java.util.Comparator;

/**
 * Stores information about an order of shares.
 */
public class Order implements Comparator<Order> {

  /**
   * The ID that should be assigned to the next order created.
   */
  private static long nextOrderID = 0;

  /**
   * The price (CENTS) of the order. Can only be in increments of 0.25.
   */
  private int price;

  /**
   * The original amount of shares this order is for.
   */
  private final int originalQuant;
  /**
   * The current amount of shares this order is for.
   */
  private int currentQuant;

  /**
   * ID number of the order.
   */
  private final long id;

  /**
   * Is true if this is an order to buy shares. False otherwise.
   */
  private final boolean buyOrder;

  /**
   * ID of the agent that initiated the order.
   */
  private final long agentID;

  /**
   * Creates an order based on the parameters specified.
   * 
   * @param agentID
   *          ID of the agent that initiated the order.
   * @param price
   *          Price (CENTS) that for the shares to buy or sell for this order.
   * @param originalQuant
   *          The quantity of shares the agent originally wants to buy or sell.
   * @param buyOrder
   *          Is true if the agent wants to initiate an order to buy shares.
   *          False otherwise.
   */
  public Order(long agentID, int price, int originalQuant, boolean buyOrder) {
    id = nextOrderID;
    nextOrderID++;
    this.agentID = agentID;
    this.buyOrder = buyOrder;
    this.originalQuant = originalQuant;
    this.price = price;
    currentQuant = originalQuant;
  }

  /**
   * @return The ID of the order.
   */
  public long getId() {
    return id;
  }

  /**
   * @return The price (CENTS) of the order.
   */
  public int getPrice() {
    return price;
  }

  /**
   * @return The original quantity of shares that the order was for.
   */
  public int getOriginalQuant() {
    return originalQuant;
  }

  /**
   * @return The current quantity of shares that the order is for.
   */
  public int getCurrentQuant() {
    return currentQuant;
  }

  /**
   * @param newQuant
   *          The new quantity of shares that the order should be for.
   */
  public void setQuant(int newQuant) {
    currentQuant = newQuant;
  }

  /**
   * @param newPrice
   *          The new price (CENTS) to buy/sell that for the order.
   */
  public void setPrice(int newPrice) {
    price = newPrice;
  }

  /**
   * @return True if the order is an order to buy shares. False otherwise.
   */
  public boolean isBuyOrder() {
    return buyOrder;
  }

  /**
   * @return The ID of the agent that initiated the order
   */
  public long getCreatorID() {
    return agentID;
  }

  /**
   * Compares the order based on price and then time of creation. An order is
   * "less than" if it has a higher price; if two orders have the same price,
   * then the one that was created first is "less than".
   * 
   * @param o1
   *          The first order to be compared.
   * @param o2
   *          The second order to be compared.
   * @return 1 if o2 has a higher price, or if both orders have the same price,
   *         1 if o2 was created first. If both orders had the same price and
   *         were created at the same time (should not happen), then 0 is
   *         returned. -1 is returned otherwise.
   */
  @Override
  public int compare(Order o1, Order o2) {
    if (o1.price != o2.price) {
      return o2.price - o1.price;
    }
    // Prices equal
    return (int) (o2.id - o1.id);
  }

  /**
   * Compares the order based on price and then time of creation. An order is
   * "less than" if it has a higher price; if two orders have the same price,
   * then the one that was created first is "less than".
   * 
   * @param o1
   *          The first order to be compared.
   * @param o2
   *          The second order to be compared.
   * @return 1 if o2 has a higher price, or if both orders have the same price,
   *         1 if o2 was created first. If both orders had the same price and
   *         were created at the same time (should not happen), then 0 is
   *         returned. -1 is returned otherwise.
   */
  public static Comparator<Order> highestFirstComparator =
    new Comparator<Order>() {
      @Override
      public int compare(Order o1, Order o2) {
        if (o1.price != o2.price) {
          return o2.price - o1.price;
        }
        return (int) (o1.id - o2.id);
      }
    };

  /**
   * Compares the order based on price and then time of creation. An order is
   * "less than" if either it has a lower price or it was created first.
   * 
   * @param o1
   *          The first order to be compared.
   * @param o2
   *          The second order to be compared.
   * @return 1 if o2 has a lower price, or if both orders have the same price, 1
   *         if o2 was created first. If both orders had the same price and were
   *         created at the same time (should not happen), then 0 is returned.
   *         -1 is returned otherwise.
   */
  public static Comparator<Order> lowestFirstComparator =
    new Comparator<Order>() {
      @Override
      public int compare(Order o1, Order o2) {
        if (o1.price != o2.price) {
          return o1.price - o2.price;
        }
        return (int) (o1.id - o2.id);
      }
    };

}
