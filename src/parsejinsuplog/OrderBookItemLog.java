/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsejinsuplog;

/**
 * 
 * @author enriqueareyan
 */
public class OrderBookItemLog {

  private float bid;

  private float ask;

  public OrderBookItemLog(float bid, float ask) {
    this.setBid(bid);
    this.setAsk(ask);
  }

  public final void setBid(float bid) {
    this.bid = bid;
  }

  public final void setAsk(float ask) {
    this.ask = ask;
  }

  public float getBid() {
    return this.bid;
  }

  public float getAsk() {
    return this.ask;
  }

  @Override
  public String toString() {
    return "Bid: " + this.bid + ", Ask: " + this.ask + ", midpoint: "
      + ((this.bid + this.ask) / 2);
  }
}
