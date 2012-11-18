import java.util.Comparator;


/**
 * Stores information about an order of shares.
 */
public class Order implements Comparator<Order>{
  
  /**
   * The price of the order. Can only be in increments of 0.25.
   */
  private double price;
  
  /**
   * The original amount of shares this order is for.
   */
  private int originalQuant;
  /**
   * The current amount of shares this order is for.
   */
  private int currentQuant;
  
  /**
   * ID number of the order.
   */
  private long id;
  
  /**
   * Is true if this is an order to buy shares. False otherwise.
   */
  private boolean buyOrder;
  
  /**
   * ID of the agent that initiated the order.
   */
  private long agentID;

  /**
   * Creates an order based on the parameters specified. The ID of the order is
   * a timestamp.
   * @param agentID ID of the agent that initiated the order.
   * @param price Price that for the shares to buy or sell for this order.
   * @param originalQuant The quantity of shares the agent originally wants to
   * buy or sell.
   * @param buyOrder Is true if the agent wants to initiate an order to buy
   * shares. False otherwise.
   */
  public Order(long agentID, double price, int originalQuant, boolean buyOrder) {
    id = System.currentTimeMillis();
    this.agentID = agentID;
    this.buyOrder = buyOrder;
    this.originalQuant  = originalQuant;
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
   * @return The price of the order.
   */
  public double getPrice() {
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
   * @param newQuant The new quantity of shares that the order should be for.
   */
  public void setQuant(int newQuant) {
    currentQuant = newQuant;
  }

  /**
   * @param newPrice The new price to buy/sell that for the order.
   */
  public void setPrice(double newPrice) {
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


//  @Override
//  public int compareTo(Order o) {
//    // TODO Auto-generated method stub
//    int priceDiff =  (int)(o.price*100) - (int)(this.price*100);
//    if(priceDiff != 0) {
//      return priceDiff;
//    }
//    else {
//      return (int) (o.id/100 - this.id/100);
//    }
//  }
  
  /**
   * Compares the order based on price and then time of creation. An order is 
   * "greater than" if either it has a higher price or it was created first.
   * @param o1 The first order to be compared.
   * @param o2 The second order to be compared.
   * @return 1 if o1 has a higher price, or if both orders have the same price,
   * 1 if o1 was created first. If both orders had the same price and were
   * created at the same time (should not happen), then 0 is returned. -1 is
   * returned otherwise.
   */
  public int compare(Order o1, Order o2) {
    // TODO Auto-generated method stub
    int priceDiff = (int)(o2.price*100) - (int)(o1.price*100);
    if(priceDiff != 0 ) {
      return priceDiff;
    } else {
      return (int) (o1.id/100 - o2.id/100);
    }
  }
  

  /**
   * Compares the order based on price and then time of creation. An order is 
   * "greater than" if either it has a lower price or it was created first.
   * @param o1 The first order to be compared.
   * @param o2 The second order to be compared.
   * @return 1 if o1 has a lower price, or if both orders have the same price,
   * 1 if o1 was created first. If both orders had the same price and were
   * created at the same time (should not happen), then 0 is returned. -1 is
   * returned otherwise.
   */
  public static Comparator<Order> lowestFirstComparator = new Comparator<Order> () {
    public int compare(Order o1, Order o2) {
      // TODO Auto-generated method stub
      int priceDiff = (int)(o1.price*100) - (int)(o2.price*100);
      if(priceDiff != 0 ) {
        return priceDiff;
      } else {
        return (int) (o2.id/100 - o1.id/100);
      }
    }
  };



}
