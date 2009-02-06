/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.feedserver.authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * An implemntation of {@link HttpServletResponse} for testing purposes
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class FakeHttpServletResponse implements HttpServletResponse {

  private HashMap<String, String> headers = new HashMap<String, String>();

  private PrintWriter pw;

  private int errorCode;

  private String errorMsg;

  private int status;

  private String statusMessage;

  public FakeHttpServletResponse() {

  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
   */
  @Override
  public void addCookie(Cookie arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String,
   * long)
   */
  @Override
  public void addDateHeader(String arg0, long arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void addHeader(String arg0, String arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String,
   * int)
   */
  @Override
  public void addIntHeader(String arg0, int arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
   */
  @Override
  public boolean containsHeader(String arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
   */
  @Override
  public String encodeRedirectURL(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
   */
  @Override
  public String encodeRedirectUrl(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
   */
  @Override
  public String encodeURL(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
   */
  @Override
  public String encodeUrl(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendError(int)
   */
  @Override
  public void sendError(int arg0) throws IOException {
    sendError(arg0, "");

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendError(int,
   * java.lang.String)
   */
  @Override
  public void sendError(int arg0, String arg1) throws IOException {
    this.errorCode = arg0;
    this.errorMsg = arg1;
    throw new IOException(errorMsg);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
   */
  @Override
  public void sendRedirect(String arg0) throws IOException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String,
   * long)
   */
  @Override
  public void setDateHeader(String arg0, long arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void setHeader(String arg0, String arg1) {
    headers.put(arg0, arg1);
  }

  public String getHeader(String headerName) {
    return headers.get(headerName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String,
   * int)
   */
  @Override
  public void setIntHeader(String arg0, int arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setStatus(int)
   */
  @Override
  public void setStatus(int arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setStatus(int,
   * java.lang.String)
   */
  @Override
  public void setStatus(int arg0, String arg1) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#flushBuffer()
   */
  @Override
  public void flushBuffer() throws IOException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getBufferSize()
   */
  @Override
  public int getBufferSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getCharacterEncoding()
   */
  @Override
  public String getCharacterEncoding() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getContentType()
   */
  @Override
  public String getContentType() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getLocale()
   */
  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getOutputStream()
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getWriter()
   */
  @Override
  public PrintWriter getWriter() throws IOException {
    return pw;
  }

  public void setWriter(PrintWriter writer) {
    pw = writer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#isCommitted()
   */
  @Override
  public boolean isCommitted() {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#reset()
   */
  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#resetBuffer()
   */
  @Override
  public void resetBuffer() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setBufferSize(int)
   */
  @Override
  public void setBufferSize(int arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
   */
  @Override
  public void setCharacterEncoding(String arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setContentLength(int)
   */
  @Override
  public void setContentLength(int arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
   */
  @Override
  public void setContentType(String arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
   */
  @Override
  public void setLocale(Locale arg0) {
    // TODO Auto-generated method stub

  }

}
