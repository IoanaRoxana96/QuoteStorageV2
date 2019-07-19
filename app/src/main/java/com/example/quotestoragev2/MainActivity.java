package com.example.quotestoragev2;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private QuoteDBManager quoteDBManager = null;
    private ListView quoteListView = null;
    private TextView quoteListEmptyTextView = null;
    private SimpleCursorAdapter listViewDataAdapter = null;


    private final String fromColumnArr[] = {QuoteDBManager.TABLE_QUOTE_COLUMN_ID, QuoteDBManager.TABLE_QUOTE_COLUMN_QUOTE};
    private final int toViewIdArr[] = {R.id.quote_item_id, R.id.quote_item_quote};

    private List<QuoteDTO> quoteCheckedItemList = new ArrayList<QuoteDTO>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote_list_view);

        setTitle("Show table rows in list view");
        quoteListView = (ListView) findViewById(R.id.quote_list_view);
        quoteListEmptyTextView = (TextView) findViewById(R.id.quote_list_empty_text_view);
        quoteListView.setEmptyView(quoteListEmptyTextView);

        //Get SQLite database query cursor
        quoteDBManager = new QuoteDBManager(getApplicationContext());
        quoteDBManager.open();
        Cursor cursor = quoteDBManager.getAllQuoteCursor();

        //Create a new SimpleCursorAdapter
        listViewDataAdapter = new SimpleCursorAdapter(this, R.layout.quote_list_view_item, cursor, fromColumnArr, toViewIdArr, CursorAdapter.FLAG_AUTO_REQUERY);
        //listViewDataAdapter.notifyDataSetChanged();

        //Set simple cursor adapter to list view
        quoteListView.setAdapter(listViewDataAdapter);


        // random


        //When list view item is clicked
        quoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long viewId) {
                //Get list view item SQLiteCursor object
                Object clickItemObject = adapterView.getAdapter().getItem(index);
                SQLiteCursor cursor = (SQLiteCursor) clickItemObject;

                //Get row column data
                int rowId = cursor.getInt(cursor.getColumnIndex(QuoteDBManager.TABLE_QUOTE_COLUMN_ID));
                String quote = cursor.getString(cursor.getColumnIndex(QuoteDBManager.TABLE_QUOTE_COLUMN_QUOTE));

                //Create a QuoteDTO object to save row column data
                QuoteDTO quoteDto = new QuoteDTO();
                quoteDto.setId(rowId);
                quoteDto.setQuote(quote);

                //Get checkbox object
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.checkbox_item);
                boolean checkboxChecked = false;
                if (itemCheckbox.isChecked()) {
                    itemCheckbox.setChecked(false);
                    checkboxChecked = false;
                } else {
                    itemCheckbox.setChecked(true);
                    checkboxChecked = true;
                }

                //Add (or remove) quoteDto from the instance variable quoteCheckedItemList
                addCheckListItem(quoteDto, checkboxChecked);

                //Show quote select list view item id
                Toast.makeText(getApplicationContext(), "Select id: " + getQuoteCheckedItemIds(), Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void addCheckListItem(QuoteDTO quoteDto, boolean add) {
        if (quoteCheckedItemList != null) {
            boolean quoteExist = false;
            int existPosition = -1;

            //Loop to check whether the quote dto exist or not
            int size = quoteCheckedItemList.size();
            for (int i = 0; i < size; i++) {
                QuoteDTO tmpDto = quoteCheckedItemList.get(i);
                if(tmpDto.getId() == quoteDto.getId()) {
                    quoteExist = true;
                    existPosition = i;
                    break;
                }
            }

            if (add) {
                //If not exist then add it
                if (!quoteExist) {
                    quoteCheckedItemList.add(quoteDto);
                }
            } else {
                //If exist then remove it
                if (quoteExist) {
                    if (existPosition != -1) {
                        quoteCheckedItemList.remove(existPosition);
                    }
                }
            }
        }
    }

    private String getQuoteCheckedItemIds() {
        StringBuffer retBuf = new StringBuffer();
        if (quoteCheckedItemList != null) {
            int size = quoteCheckedItemList.size();
            for (int i = 0; i < size; i++) {
                QuoteDTO tmpDto = quoteCheckedItemList.get(i);
                retBuf.append(tmpDto.getId());
                retBuf.append(" ");
            }
        }
        return retBuf.toString().trim();
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu) {
            //Inflate the action bar menu
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.action_bar, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int itemId = item.getItemId();
            if (itemId == R.id.menu_add) {
                AddQuote.start(getApplicationContext(), -1, "");
            }  else if (itemId == R.id.menu_delete) {
                if (quoteCheckedItemList != null) {
                    int size = quoteCheckedItemList.size();
                    if (size == 0) {
                        Toast.makeText(this, "Select a row to delete", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < size; i++) {
                            QuoteDTO tmpDto = quoteCheckedItemList.get(i);
                            quoteDBManager.deleteQuote(tmpDto.getId());

                            quoteCheckedItemList.remove(i);
                            size = quoteCheckedItemList.size();
                            i--;
                        }

                        //Reload quote data from SQLite database
                        Cursor cursor = quoteDBManager.getAllQuoteCursor();
                        listViewDataAdapter = new SimpleCursorAdapter(this, R.layout.quote_list_view_item, cursor, fromColumnArr, toViewIdArr, CursorAdapter.FLAG_AUTO_REQUERY);
                        //Set new data adapter to lise view
                        quoteListView.setAdapter(listViewDataAdapter);
                    }
                }
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (quoteDBManager != null) {
                quoteDBManager.close();
                quoteDBManager = null;
            }
        }

     private static final String SQL_RANDOM_QUOTE = "SELECT quote FROM Quote OREDER BY RANDOM() LIMIT 1";
}




