package com.google.feedserver.ibatis;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class SqlArrayToArrayConverter implements TypeHandlerCallback {

  public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Object getResult(ResultGetter getter) throws SQLException {
    Array array = getter.getResultSet().getArray(getter.getColumnName());
    if (!getter.getResultSet().wasNull()) {
      printArray(array.getArray());
      return array.getArray();
    } else {
      return null;
    }
  }

  public Object valueOf(String s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  protected void printArray(Object a) {
    Object[] array = (Object[]) a;
    System.out.print('[');
    for (Object e : array) {
      if (e !=  null && e instanceof BigDecimal) {
        System.out.print("###" + e + "###");
      } else {
        System.out.print(e);
      }
      System.out.print(',');
    }
    System.out.println(']');
  }
}
