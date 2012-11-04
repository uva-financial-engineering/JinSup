import java.util.Comparator;


public class Order implements Comparable<Order>{
  private double price;
  private int originalQuant;
  private int currentQuant;
  private long id;
  private boolean buyOrder; //true if this is a buy order.
  private long agentID; //id of the agent that initiated the order

  public Order(long agentID, double price, int originalQuant, boolean buyOrder) {
    id = System.currentTimeMillis();
    this.agentID = agentID;
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

  public boolean isBuyOrder() {
    return buyOrder;
  }

  public long getCreatorID() {
    return agentID;
  }


  @Override
  public int compareTo(Order o) {
    // TODO Auto-generated method stub
    if((int)(this.price*100) - (int)(o.price*100) != 0) {
      return -1*((int)(this.price*100) - (int)(o.price*100));
    }
    else {
      return (int) (this.id/100 - o.id/100);
    }
  }



}
