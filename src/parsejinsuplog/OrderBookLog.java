/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsejinsuplog;

import java.util.Map;

/**
 * 
 * @author enriqueareyan
 */
public class OrderBookLog {

  /*
   * The order Book is a TreeMap with Time and best bid/ask price
   */
  private Map<Long, OrderBookItemLog> OrderBook;

  /*
   * Constructor
   */
  public OrderBookLog(Map<Long, OrderBookItemLog> OrderBook) {
    this.setOrderBookLog(OrderBook);
  }

  /*
   * setter for OrderBookLog
   */
  public final void setOrderBookLog(Map<Long, OrderBookItemLog> OrderBook) {
    this.OrderBook = OrderBook;
  }

  /*
   * Getter for OrderBookLog
   */
  public Map<Long, OrderBookItemLog> getOrderBookLog() {
    return this.OrderBook;
  }

  /*
   * utility method to print order book log
   */
  public void printOrderBookLog() {
    int i = 0;
    for (Long key : this.OrderBook.keySet()) {
      System.out.println(key + "-" + this.OrderBook.get(key));
      i++;
    }
    System.out
      .println("There are " + i + " entries in total in the order book");
  }
}
