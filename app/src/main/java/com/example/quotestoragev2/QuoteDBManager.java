package com.example.quotestoragev2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class QuoteDBManager {
    private Context ctx;
    private DatabaseManager dbManager;
    private static final String DB_QUOTE = "Quote.db";
    private static final String TABLE_NAME_QUOTE = "quote";

    //SQLite database need table primary key column named as _id
    public static final String TABLE_QUOTE_COLUMN_ID = "_id";
    public static final String TABLE_QUOTE_COLUMN_QUOTE = "quote";

    private int DB_VERSION = 1;
    List<String> tableQuoteList = null;
    List<String> createTableSqlList = null;

    public QuoteDBManager (Context ctx) {
        this.ctx = ctx;
        this.init();
        this.dbManager = new DatabaseManager(ctx, this.DB_QUOTE, this.DB_VERSION, this.tableQuoteList, this.createTableSqlList);

    }


    private void init() {
        if(this.tableQuoteList == null) {
            this.tableQuoteList = new ArrayList<String>();
        }
        if(this.createTableSqlList == null) {
            this.createTableSqlList = new ArrayList<String>();
        }
        this.tableQuoteList.add(TABLE_NAME_QUOTE);

        //Build create quote table sql
        StringBuffer sqlBuf = new StringBuffer();

        //Create table sql
        sqlBuf.append("create table");
        sqlBuf.append(TABLE_NAME_QUOTE);
        sqlBuf.append("( ");
        sqlBuf.append(TABLE_QUOTE_COLUMN_ID);
        sqlBuf.append("integer primary key autoincrement,");
        sqlBuf.append(TABLE_QUOTE_COLUMN_QUOTE);
        sqlBuf.append(" text )");
        this.createTableSqlList.add(sqlBuf.toString());
    }

    public void open() {
        this.dbManager.openDB();
    }

    public void close() {
        this.dbManager.closeDB();
    }

    //Insert one row
    public void insertQuote(String quote) {
        //Create table column list
        List<TableColumn> tableColumnList = new ArrayList<TableColumn>();

        //Add quote column
        TableColumn quoteColumn = new TableColumn();
        quoteColumn.setColumnName(this.TABLE_QUOTE_COLUMN_QUOTE);
        quoteColumn.setColumnValue(quote);
        tableColumnList.add(quoteColumn);

        //Insert added column in to quote table
        this.dbManager.insert(this.TABLE_NAME_QUOTE, tableColumnList);
    }

    //Update one row
    public void updateQuote (int id, String quote) {
        //Create table column list
        List<TableColumn> updateColumnList = new ArrayList<TableColumn>();

        //Update quote cannot be empty
        if (!TextUtils.isEmpty(quote)) {
            TableColumn quoteColumn = new TableColumn();
            quoteColumn.setColumnName(this.TABLE_QUOTE_COLUMN_QUOTE);
            quoteColumn.setColumnValue(quote);
            updateColumnList.add(quoteColumn);
        }

        String whereClause = this.TABLE_QUOTE_COLUMN_ID + "=" + id;

        this.dbManager.update(this.TABLE_NAME_QUOTE, updateColumnList, whereClause);
    }

        //Delete one quote
        public void deleteQuote (int id) {
        this.dbManager.delete(this.TABLE_NAME_QUOTE, this.TABLE_QUOTE_COLUMN_ID + "=" + id);
    }

    //Get all quote dto list
    public List<QuoteDTO> getAllQuote() {
        List<QuoteDTO> ret = new ArrayList<QuoteDTO>();
        Cursor cursor = this.dbManager.queryAllReturnCursor(this.TABLE_NAME_QUOTE);
        if(cursor != null) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(this.TABLE_QUOTE_COLUMN_ID));
                String quote = cursor.getString(cursor.getColumnIndex(this.TABLE_QUOTE_COLUMN_QUOTE));

                QuoteDTO quoteDto = new QuoteDTO();
                quoteDto.setId(id);
                quoteDto.setQuote(quote);

                ret.add(quoteDto);
            }
            while(cursor.moveToNext());

            //Close cursor after query
            if(!cursor.isClosed()) {
                cursor.close();
            }
        }
        return ret;
    }

    //Return sqlite database cursor object
    public Cursor getAllQuoteCursor() {
        Cursor cursor = this.dbManager.queryAllReturnCursor(this.TABLE_NAME_QUOTE);
        return cursor;
    }
}
