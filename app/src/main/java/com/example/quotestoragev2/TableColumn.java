package com.example.quotestoragev2;

public class TableColumn {

  private String columnName;
  private String columnValue;

  public String getColumnName() {
      return columnName;
  }

  public String getColumnValue() {
      return columnValue;
  }

  public void setColumnValue(String columnValue) {
      this.columnValue = columnValue;
  }

  public void setColumnName(String columnName) {
      this.columnName = columnName;
  }
}
