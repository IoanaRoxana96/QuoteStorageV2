package com.example.quotestoragev2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    //Contains all table name string list
    private List<String> tableNameList = null;

    //Contains all create table sql command string list
    private List<String> createTableSQList = null;

    //This is the log tag in android monitor console
    public static final String LOG_TAG_SQLITE_DB = "LOG_TAG_SQLITE_DB";

    //This is the android activity context
    private Context ctx = null;

    //Constructor with all input parameter
    public SQLiteDatabaseHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
        ctx = context;
    }

    //Run all create table sql in this method
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(createTableSQList != null) {
            int size = createTableSQList.size();
            for (int i = 0; i < size; i++) {
                String createTableSql = createTableSQList.get(i);
                sqLiteDatabase.execSQL(createTableSql);

                Toast.makeText(ctx, "Run sql successfully, " + createTableSql, Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(tableNameList != null) {
            int size = tableNameList.size();
            for (int i = 0; i < size; i++) {
                String tableName = tableNameList.get(i);
                if(!TextUtils.isEmpty(tableName)) {
                    sqLiteDatabase.execSQL("drop table if exists" + tableName);
                }
            }
        }

        onCreate(sqLiteDatabase);
    }

    public List<String> getTableNameList() {
        if(tableNameList == null) {
            tableNameList = new ArrayList<String>();
        }
        return tableNameList;
    }

    public void setTableNameList(List<String> tableNameList) {
        this.tableNameList = tableNameList;
    }

    public List<String> getCreateTableSQList() {
        if(createTableSQList == null) {
            createTableSQList = new ArrayList<String>();
        }
        return createTableSQList;
    }

    public void setCreateTableSQList(List<String> createTableSQList) {
        this.createTableSQList = createTableSQList;
    }


}
