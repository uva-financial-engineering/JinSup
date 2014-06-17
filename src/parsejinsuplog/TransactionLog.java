/*
 * This class represents one transaction. An OrderLog is composed of TransactionLog.
 */
package parsejinsuplog;

/**
 * 
 * @author enriqueareyan
 */
public final class TransactionLog {
  /*
   * time of the transaction in milliseconds. This times goes from 0 up to the
   * length of the simulation.
   */
  private long time;
  /*
   * message of the transaction. 1 = new order, 2 = modification, 3 = cancel,
   * 105 = trade.
   */
  private int message;
  /*
   * true for buy order, false for sell order
   */
  private boolean buyOrder;
  /*
   * Original Quantity: the quantity of shares to be bought or sold.
   */
  private int originalQuant;
  /*
   * price of share.
   */
  private float price;
  /*
   * true for market order, false for limit order
   */
  private boolean marketOrder;
  /*
   * how many of the original quantity were left unfulfilled. Hence, Leaves
   * Quantity <= Original Quantity. If Leaves Quantity = Original Quantity, then
   * there was no trade. If Leaves Quantity = 0, then the order was completed.
   */
  private int leavesQuantity;
  /*
   * Trade Price: the price at which the transaction occurred.
   */
  private float tradePrice;
  /*
   * Quantity Filled: how many units out of the Original Quantity were fulfilled
   * in this transaction.
   */
  private int quantityFilled;
  /*
   * Aggressor: Only for traded orders. Y if the corresponding agent initiated
   * the trade by crossing the best bid/ask threshold, N otherwise.
   */
  private char aggressor;
  /*
   * Trade Match ID: Unique identifier for the trade. Simply the number of
   * milliseconds since the Unix Epoch.
   */
  private long tradeMatchID;

  /*
   * constructor.
   */
  public TransactionLog(long time, int message, boolean buyOrder,
    int originalQuant, float price, boolean marketOrder, int leavesQuantity,
    float tradePrice, int quantityFilled, char aggressor, long tradeMatchID) {
    this.setTime(time);
    this.setMessage(message);
    this.setbuyOrder(buyOrder);
    this.setoriginalQuant(originalQuant);
    this.setprice(price);
    this.setmarketOrder(marketOrder);
    this.setleavesQuantity(leavesQuantity);
    this.settradePrice(tradePrice);
    this.setquantityFilled(quantityFilled);
    this.setaggressor(aggressor);
    this.settradeMatchID(tradeMatchID);
  }

  /*
   * set time
   */
  private void setTime(long time) {
    this.time = time;
  }

  /*
   * get time
   */
  public long getTime() {
    return this.time;
  }

  /*
   * set message
   */
  private void setMessage(int message) {
    this.message = message;
  }

  /*
   * get message
   */
  public int getMessage() {
    return this.message;
  }

  /*
   * set whether the order is buy or sell
   */
  private void setbuyOrder(boolean buyOrder) {
    this.buyOrder = buyOrder;
  }

  /*
   * get whether the order is buy or sell
   */
  public boolean getbuyOrder() {
    return this.buyOrder;
  }

  /*
   * set original quantity
   */
  private void setoriginalQuant(int originalQuant) {
    this.originalQuant = originalQuant;
  }

  /*
   * get original quantity
   */
  public int getoriginalQuant() {
    return this.originalQuant;
  }

  /*
   * set price
   */
  private void setprice(float price) {
    this.price = price;
  }

  /*
   * get price
   */
  public float getprice() {
    return this.price;
  }

  /*
   * set whether the order is market or limit
   */
  private void setmarketOrder(boolean marketOrder) {
    this.marketOrder = marketOrder;
  }

  /*
   * get whether the order is market or limit
   */
  public boolean getmarketOrder() {
    return this.marketOrder;
  }

  /*
   * set quantity left
   */
  private void setleavesQuantity(int leavesQuantity) {
    this.leavesQuantity = leavesQuantity;
  }

  /*
   * get quantity left
   */
  public int getleavesQuantity() {
    return this.leavesQuantity;
  }

  /*
   * set the price at which the transaction occurred
   */
  private void settradePrice(float tradePrice) {
    this.tradePrice = tradePrice;
  }

  /*
   * get the price at which the transaction occurred
   */
  public float gettradePrice() {
    return this.tradePrice;
  }

  /*
   * set quantity filled
   */
  private void setquantityFilled(int quantityFilled) {
    this.quantityFilled = quantityFilled;
  }

  /*
   * get quantity filled
   */
  public int getquantityFilled() {
    return this.quantityFilled;
  }

  /*
   * set aggressor
   */
  private void setaggressor(char aggressor) {
    this.aggressor = aggressor;
  }

  /*
   * get aggressor
   */
  public char getaggressor() {
    return this.aggressor;
  }

  /*
   * set trade match ID
   */
  private void settradeMatchID(long tradeMatchID) {
    this.tradeMatchID = tradeMatchID;
  }

  /*
   * get trade match ID
   */
  public long gettradeMatchID() {
    return this.tradeMatchID;
  }

  /*
   * string representation of this object
   */
  @Override
  public String toString() {
    return "time: " + this.getTime() + ", message: " + this.getMessage()
      + ", buyOrder: " + this.getbuyOrder() + ", originalQuant: "
      + this.getoriginalQuant() + ", price: " + this.getprice()
      + ", type (market?): " + this.getmarketOrder() + ", leavesQuantity: "
      + this.getleavesQuantity() + ", tradePrice:" + this.gettradePrice()
      + ", quantityFilled: " + this.getquantityFilled() + ", aggressor: "
      + this.getaggressor() + ", tradeMatchID: " + this.gettradeMatchID()
      + "\n";
  }
}