package edu.virginia.jinsup;

/**
 * Keeps track of delay data for Intelligent Agents without cluttering
 * MatchingEngine.
 */
public class IntelligentAgentHelper {

  private int delayLength;
  private int[] volumeDifferenceData;
  private int[] tradePriceData;
  private int oldestIndex;

  public IntelligentAgentHelper(int delayLength) {
    this.delayLength = delayLength;
    volumeDifferenceData = new int[delayLength];
    tradePriceData = new int[delayLength];
    oldestIndex = 0;
  }

  /**
   * Get the oldest volume difference data. Must ensure this is called first
   * before adding new data otherwise it will be overwritten.
   * 
   * @return The oldest data so far.
   */
  public int getOldVolumeDifferenceData() {
    return volumeDifferenceData[oldestIndex];
  }

  /**
   * Get the oldest tradePrice data. Must ensure this is called first before
   * adding new data otherwise it will be overwritten.
   * 
   * @return The oldest data so far.
   */
  public int getOldTradePriceData() {
    return tradePriceData[oldestIndex];
  }

  /**
   * Adds new data. Wraps around the array if necessary.
   * 
   * @param newData
   */
  public void addData(int newVolumeDifference, int newTradePrice) {
    volumeDifferenceData[oldestIndex] = newVolumeDifference;
    tradePriceData[oldestIndex] = newTradePrice;
    oldestIndex++;
    if (oldestIndex >= delayLength) {
      oldestIndex = 0;
    }
  }
}