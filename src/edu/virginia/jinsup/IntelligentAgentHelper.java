package edu.virginia.jinsup;

import java.util.Arrays;

/**
 * Keeps track of delay data for Intelligent Agents without cluttering
 * MatchingEngine.
 */
public class IntelligentAgentHelper {

  private int delayLength;
  private int[] volumeDifferenceData;
  private int[] tradePriceData;
  private int oldestIndex;
  private int threshold;
  private int currentTradePriceDifference;

  public enum ThresholdState {
    BELOW_THRESHOLD, BUY_ORDER_SURPLUS, SELL_ORDER_SURPLUS;
  }

  private ThresholdState currentThresholdState;

  public IntelligentAgentHelper(int delayLength, int threshold,
    int initialTradePrice) {
    this.delayLength = delayLength;
    volumeDifferenceData = new int[delayLength];
    tradePriceData = new int[delayLength];
    Arrays.fill(tradePriceData, initialTradePrice);
    oldestIndex = 0;
    this.threshold = threshold;
    currentThresholdState = ThresholdState.BELOW_THRESHOLD;
    currentTradePriceDifference = 0;
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
   * Get the oldest trade price data. Must ensure this is called first before
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
    int previousOldTradePrice = tradePriceData[oldestIndex];
    volumeDifferenceData[oldestIndex] = newVolumeDifference;
    tradePriceData[oldestIndex] = newTradePrice;
    currentThresholdState = computeThresholdState(newVolumeDifference);
    oldestIndex++;
    if (oldestIndex >= delayLength) {
      oldestIndex = 0;
    }
    currentTradePriceDifference =
      previousOldTradePrice - getOldTradePriceData();
    if (currentTradePriceDifference != 0) {
      System.out.println(currentTradePriceDifference);
    }
  }

  /**
   * Determines the threshold state from the sum of orders at the best bid/ask.
   * 
   * @param volumeDifference
   *          Total volume at best bid - total volume at best ask.
   * @return The ThresholdState.
   */
  private ThresholdState computeThresholdState(int volumeDifference) {
    if (Math.abs(volumeDifference) < threshold) {
      return ThresholdState.BELOW_THRESHOLD;
    } else if (volumeDifference < threshold) {
      return ThresholdState.SELL_ORDER_SURPLUS;
    } else {
      return ThresholdState.BUY_ORDER_SURPLUS;
    }
  }

  /**
   * @return TradePrice[i - 1] - TradePrice[i].
   */
  public int getTradePriceDifference() {
    return currentTradePriceDifference;
  }

  public ThresholdState getCurrentThresholdState() {
    return currentThresholdState;
  }

  public int getOldestIndex() {
    return oldestIndex;
  }
}