/*
 *  This class represents an order. It is composed of TransactionLog.
 */
package parsejinsuplog;

import java.util.*;

/**
 * 
 * @author enriqueareyan
 */
public final class OrderLog {
  /*
   * Order ID.
   */
  private long orderID;

  /*
   * Array of transaction log objects with each transaction for this order.
   */
  private ArrayList<TransactionLog> Transactions = new ArrayList<TransactionLog>();

  /*
   * Constructor for the order
   */
  public OrderLog(long orderID) {
    this.setorderID(orderID);
  }

  /*
   * set order id
   */
  private void setorderID(long orderID) {
    this.orderID = orderID;
  }

  /*
   * get order id
   */
  public long getOrderID() {
    return this.orderID;
  }

  /*
   * add a transaction to this order
   */
  public void addTransaction(long time, int message, boolean buyOrder,
    int originalQuant, float price, boolean marketOrder, int leavesQuantity,
    float tradePrice, int quantityFilled, char aggressor, long tradeMatchID) {
    TransactionLog T =
      new TransactionLog(time, message, buyOrder, originalQuant, price,
        marketOrder, leavesQuantity, tradePrice, quantityFilled, aggressor,
        tradeMatchID);
    this.Transactions.add(T);

  }

  /*
   * Get transactions
   */
  public ArrayList<TransactionLog> getTransactions() {
    return this.Transactions;
  }

  /*
   * string representation of this object
   */
  @Override
  public String toString() {
    int i = 0;
    String buffer = "";
    buffer = "ID: " + this.getOrderID();
    buffer =
      buffer + "\n\tThere are " + this.Transactions.size() + " transactions:";
    Iterator<TransactionLog> it = Transactions.iterator();
    while (it.hasNext()) {
      i++;
      TransactionLog obj = it.next();
      buffer = buffer + "\n\t\t\t" + i + "==> " + obj;
    }
    return buffer;
  }
}
