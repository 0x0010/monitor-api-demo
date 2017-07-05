package com.example.monitor.api.demo;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

/**
 * @author Sam
 * @since 3.0.0
 */
public class Monitor {

  /**
   * Metrics注册器
   */
  private Object metricRegistry;
  private Meter idxTpsMeter;

  private static final Monitor MONITOR = new Monitor();

  private Monitor() {
    try {
      metricRegistry = Class.forName("com.codahale.metrics.MetricRegistry").newInstance();
      idxTpsMeter = ((MetricRegistry) metricRegistry)
          .meter(MetricRegistry.name("proc.tps", "api_index"));
    } catch (Throwable throwable) {
      metricRegistry = null;
    }
  }

  static Object getMetricRegistry() {
    return MONITOR.metricRegistry;
  }

  public static void idxTpsMark() {
    MONITOR.idxTpsMeter.mark();
  }

  public static Meter getIdxTpsMeter() {
    return MONITOR.idxTpsMeter;
  }
}
