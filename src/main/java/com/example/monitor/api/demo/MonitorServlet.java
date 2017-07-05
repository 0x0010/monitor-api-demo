package com.example.monitor.api.demo;

import com.codahale.metrics.Meter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sam
 * @since 3.0.0
 */
public class MonitorServlet extends HttpServlet {

  private static Logger log = LoggerFactory.getLogger(MonitorServlet.class);

  private static final long serialVersionUID = 734398626820571179L;
  private ServletHandler servletHandler;

  @Override
  public void init(ServletConfig pServletConfig) throws ServletException {
    servletHandler = new DefaultServletHandler();
    Object registry = Monitor.getMetricRegistry();
    if (null == registry) {
      log.info("Monitor can't be use, because no metric registry can be found in current context.");
    }
    super.init(pServletConfig);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    sendStreamingResponse(resp, servletHandler.handleRequest(req.getRequestURI()));
  }

  private void sendStreamingResponse(HttpServletResponse pResp, String jsonStr) throws IOException {
    ChunkedWriter writer = null;
    try {
      writer = new ChunkedWriter(pResp.getOutputStream(), "UTF-8");
      writer.write(jsonStr);
    } finally {
      if (writer != null) {
        writer.flush();
        writer.close();
      }
    }
  }

  private class DefaultServletHandler extends ServletHandler {

    private final String[] COL_IDX_PROC_TPS =
        new String[]{"idxCount", "idxMeanRate", "idx1mRate", "idx5mRate", "idx15mRate"};

    private final String[] COL_PROC_MEM_INFO = new String[]{"heapSize", "heapMaxSize"};


    @Override
    protected String getIdxTps() {
      Meter meter = Monitor.getIdxTpsMeter();
      return
          JSONObject.toJSONString(
              newMap(
                  COL_IDX_PROC_TPS,
                  new Object[]{
                      meter.getCount(),
                      meter.getMeanRate(), meter.getOneMinuteRate(),
                      meter.getFiveMinuteRate(), meter.getFifteenMinuteRate()}
              ));
    }

    @Override
    protected String getMemInfo() {
      return JSONObject.toJSONString(
          newMap(
              COL_PROC_MEM_INFO,
              new Object[]{
                  Runtime.getRuntime().totalMemory(),
                  Runtime.getRuntime().maxMemory()
              }
          ));
    }

    private Map<String, Object> newMap(String[] keyArray, Object[] valueArray) {
      Map<String, Object> map = new HashMap<>();
      for (int i = 0, len = keyArray.length; i < len; i++) {
        map.put(keyArray[i], valueArray[i]);
      }
      return map;
    }
  }
}
