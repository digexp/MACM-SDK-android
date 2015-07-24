package com.ibm.caas;

/**
 * Measures the time taken by one or more http requests.
 */
class PerformanceMeasurement {
  double firstTime = -1L;
  double totalTime = 0d;
  int nbRequests = 0;
  double avgTime = 0d;
  double minTime = Double.MAX_VALUE;
  double maxTime = 0d;

  /**
   * Add a new HTTP request time and recompute the stats accordingly.
   * @param time the request time.
   */
  void newTime(double time) {
    nbRequests++;
    totalTime += time;
    if (firstTime < 0L) firstTime = time;
    if (time < minTime) minTime = time;
    if (time > maxTime) maxTime = time;
    avgTime = totalTime / nbRequests;
  }

  @Override
  public String toString() {
    return String.format("number of requests = %,d, first request time = %,.0f ms, average time = %,.0f ms, min time = %,.0f ms, max time = %,.0f ms",
      nbRequests, firstTime, avgTime, minTime, maxTime);
  }
}
