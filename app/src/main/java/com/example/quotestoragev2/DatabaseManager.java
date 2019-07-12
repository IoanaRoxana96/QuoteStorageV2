package com.example.quotestoragev2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private Context ctx = null;

    private SQLiteDatabaseHelper dbHelper = null;

    private SQLiteDatabase database = null;

    private String dbName = "";

    private int dbVersion = 0;

    private List<String> tableNameList = null;

    private List<String> createTableSqlLite = null;

    public DatabaseManager (Context ctx, String dbName, int dbVersion, List<String> tableNameList, List<String> createTableSqlLite) {
        this.ctx = ctx;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        this.tableNameList = tableNameList;
        this.createTableSqlLite = createTableSqlLite;
    }



    public DatabaseManager openDB() {

        dbHelper = new SQLiteDatabaseHelper(ctx, this.dbName, null, this.dbVersion);
        dbHelper.setTableNameList(this.tableNameList);
        dbHelper.setCreateTableSQList(this.createTableSqlLite);

        this.database = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDB() {
        this.database.close();
        this.dbHelper.close();
        this.database = null;
        this.dbHelper = null;
    }

    public void insert(String tableName, List<TableColumn> columnList) {
        if(!TextUtils.isEmpty(tableName) && columnList != null) {
            int size = columnList.size();
            if(size > 0) {
                ContentValues contentValues = new ContentValues();
                for(int i = 0; i< size; i++) {
                    TableColumn tableColumn= columnList.get(i);

                    if(!TextUtils.isEmpty(tableColumn.getColumnName())) {
                        contentValues.put(tableColumn.getColumnName(), tableColumn.getColumnValue());
                    }
                }
                this.database.insert(tableName, null, contentValues);

            }
        }
    }

    public int update(String tableName, List<TableColumn> columnList, String whereClause) {
        int ret = 0;
        if(!TextUtils.isEmpty(tableName) && columnList != null) {
            int size = columnList.size();
            if (size > 0) {
                ContentValues contentValues = new ContentValues();

                for (int i = 0; i < size; i++) {
                    TableColumn tableColumn = columnList.get(i);
                    if(!TextUtils.isEmpty(tableColumn.getColumnName())) {
                        contentValues.put(tableColumn.getColumnName(), tableColumn.getColumnValue());
                    }
                }

                ret = this.database.update(tableName, contentValues, whereClause, null);
            }
        }
        return ret;
    }

    public void delete(String tableName, String whereClause) {

        if(!TextUtils.isEmpty(tableName)) {
            this.database.delete(tableName, whereClause, null);
        }
    }

    public List<Map<String, String>> queryAllReturnListMap(String tableName) {
        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        Cursor cursor  = this.database.query(tableName, null, null, null, null, null, null);
        if(cursor != null) {
            String columnNamesArr[] = cursor.getColumnNames();
            cursor.moveToFirst();
            do {
                Map<String, String> rowMap = new HashMap<String, String>();

                int columnCount = columnNamesArr.length;
                for (int i = 0; i< columnCount; i++) {
                    String columnName = columnNamesArr[i];
                    String columnValue = "";
                    int columnIndex = cursor.getColumnIndex(columnName);
                    int columnType = cursor.getType(columnIndex);
                    if(Cursor.FIELD_TYPE_STRING == columnType) {
                        columnValue = cursor.getString(columnIndex);
                    } else if(Cursor.FIELD_TYPE_INTEGER == columnType) {
                        columnValue = String.valueOf(cursor.getInt(columnIndex));
                    } else if (Cursor.FIELD_TYPE_BLOB == columnType) {
                        columnValue = String.valueOf(cursor.getInt(columnIndex));
                    } else if (Cursor.FIELD_TYPE_FLOAT == columnType) {
                        columnValue = String.valueOf(cursor.getInt(columnIndex));
                    } else if (Cursor.FIELD_TYPE_NULL == columnType) {
                        columnValue = "null";
                    }
                    rowMap.put(columnName, columnValue);
                    ret.add(rowMap);
                }
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return ret;
    }

    public Cursor queryAllReturnCursor(String tableName){
        Cursor cursor = this.database.query(tableName, null, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

}
