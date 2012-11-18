
public class Agent {
  

  private long id;
  private MatchingEngine matchEng;
  private long nextActTime;
  private boolean willAct;
  private int inventory; //number of shares bought or sold. -ve if only sold, +ve if only bought
  private boolean lastOrderTraded;

  public Agent(MatchingEngine matchEng) {
    this.id = System.currentTimeMillis();
    this.matchEng = matchEng;
    this.inventory = 0;
    this.nextActTime = -1;
  }

  public void act() {
    return;
  }


  public long getNextActTime() {
    return nextActTime;
  }

  public boolean getWillAct() {
    return willAct;
  }

  public void setWillAct(boolean act) {
    willAct = act;
  }


  public void cancelOrder(Order o) {
    matchEng.cancelOrder(o, this.id);
  }

  public void createNewOrder(double price, int initialQuant, boolean buyOrder) {
    Order newOrder = new Order(this.id, price, initialQuant, buyOrder);
    matchEng.createOrder(newOrder, this.id);
  }

  public void modifyOrder(Order o, double newPrice, int newQuant) {
    matchEng.modifyOrder(o, newPrice, newQuant);
  }

  public void setLastOrderTraded(boolean traded, int volume) {
    lastOrderTraded = traded;
    inventory += volume;
  }

}