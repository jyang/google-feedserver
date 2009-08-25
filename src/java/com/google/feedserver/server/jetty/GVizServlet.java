package com.google.feedserver.server.jetty;

import com.google.feedserver.util.XmlUtil;
import com.google.gdata.util.common.base.StringUtil;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;

import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

public class GVizServlet extends AbderaServlet {

  private final static Log log = LogFactory.getLog(GVizServlet.class);

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
  throws IOException {
    RequestContext reqcontext =
      new ServletRequestContext(provider, request, getServletContext());
    FilterChain chain = new FilterChain(provider,reqcontext);
    try {
      output(request, response, chain.next(reqcontext));
    } catch (Throwable t) {
      error("Error servicing request", t, response);
      return;
    }
    log.debug("Request complete");
  }

  private void output(HttpServletRequest request, HttpServletResponse response,
      ResponseContext context)
  throws IOException {
    if (context != null) {
      response.setStatus(context.getStatus());
      long cl = context.getContentLength();
      String cc = context.getCacheControl();
      if (cl > -1) response.setHeader("Content-Length", Long.toString(cl));
      if (cc != null && cc.length() > 0) response.setHeader("Cache-Control",cc);
      try {
        MimeType ct = context.getContentType();
        if (ct != null) response.setContentType(ct.toString());
      } catch (Exception e) {}
      String[] names = context.getHeaderNames();
      for (String name : names) {
        Object[] headers = context.getHeaders(name);
        for (Object value : headers) {
          if (value instanceof Date)
            response.setDateHeader(name, ((Date)value).getTime());
          else
            response.setHeader(name, value.toString());
        }
      }
      if ("gviz".equalsIgnoreCase(request.getParameter("out"))) {      
        outputGViz(request, response, context);
      }
      else if (!request.getMethod().equals("HEAD") && context.hasEntity()) {
        context.writeTo(response.getOutputStream());
      }
    } else {
      error("Internal Server Error", null, response);
    }
  }

  private void outputGViz(HttpServletRequest request, HttpServletResponse response,
      ResponseContext context) throws IOException {
    String result = null;
    try {
      result = getGVizContent(context);
    } catch (Exception e) {
      throw new IOException(e);
    }
    response.setHeader("Content-Length", Integer.toString(result.getBytes().length));
    response.setContentType("application/json");
    response.getWriter().print(result);
    response.getWriter().flush();   
  }

  private String getGVizContent(ResponseContext context) throws IOException, SAXException, ParserConfigurationException {
    StringWriter writer = new StringWriter();
    context.writeTo(writer);
    String[] path = new String[] {"feed", "entry", "content", "entity", "GvizEntry"};
    Map properties = new XmlUtil().convertXmlToProperties(writer.toString());
    Object value = null;
    for (String key : path) {
      value = properties.get(key);
      if (value instanceof Map) {
        properties = (Map)value;
      }      
    }
    return value.toString();
  }

  private void error(String message, Throwable t, HttpServletResponse response)
  throws IOException {
    if (t != null) log.error(message, t);
    else log.error(message);

    if (response.isCommitted()) {
      log.error("Could not write an error message as the headers & HTTP status were already committed!");
    } else {
      response.setStatus(500);
      StreamWriter sw = getAbdera().newStreamWriter().setOutputStream(
          response.getOutputStream(), "UTF-8");
      Error.create(sw, 500, message,t);
      sw.close();
    }
  }
}
