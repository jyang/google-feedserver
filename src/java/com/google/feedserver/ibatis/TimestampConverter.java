package com.google.feedserver.ibatis;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Converts between database timestamp and java.sql.Timestamp.
 */
public class TimestampConverter implements TypeHandlerCallback {

  @Override
  public Object getResult(ResultGetter getter) throws SQLException {
//    return getter.getObject();
    Object value = getter.getObject();
    return value;
  }

  @Override
  public void setParameter(ParameterSetter setter, Object parameter)
      throws SQLException {
    // TODO Auto-generated method stub
    int x = 0;
  }

  @Override
  public Object valueOf(String s) {
    // TODO Auto-generated method stub
    return null;
  }
}
