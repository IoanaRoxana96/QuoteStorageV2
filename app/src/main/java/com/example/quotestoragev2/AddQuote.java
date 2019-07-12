package com.example.quotestoragev2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;

public class AddQuote extends AppCompatActivity {
    private QuoteDBManager quoteDBManager = null;
    private static final String INPUT_ID = "INPUT_ID";
    private static final String INPUT_QUOTE = "INPUT_QUOTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Add quote");
        //Get quote editor control
        final EditText quoteEditor = (EditText) findViewById(R.id.quote);

        //Get input extra quote data from QuoteListViewActivity activity
        Intent intent = getIntent();
        final int quoteId = intent.getIntExtra(INPUT_ID, -1);
        String quote = intent.getStringExtra(INPUT_QUOTE);

        //Cannot edit exist quote
        if(quoteId != -1) {
            quoteEditor.setText(quote);
            quoteEditor.setEnabled(false);
        }

        //Open SQLite database connection
        quoteDBManager = new QuoteDBManager(getApplicationContext());
        quoteDBManager.open();

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quote = quoteEditor.getText().toString();

                StringBuffer errorMessageBuf = new StringBuffer();

                if(TextUtils.isEmpty(quote)) {
                    errorMessageBuf.append("Quote cannot be empty/r/n");
                }
                if(errorMessageBuf.length() > 0) {
                    Toast.makeText(getApplicationContext(), errorMessageBuf.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    if(quoteId == -1) {
                        //Insert new quote
                        quoteDBManager.insertQuote(quote);
                    } else {
                        //Update exist quote
                        quoteDBManager.updateQuote(quoteId, quote);
                    }
                    Toast.makeText(getApplicationContext(), errorMessageBuf.toString(), Toast.LENGTH_SHORT).show();
                    finish();

                    Intent startQuoteListIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startQuoteListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startQuoteListIntent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(quoteDBManager != null) {
            quoteDBManager.close();
            quoteDBManager = null;
        }
    }

    //Start this activity from other class
    public static void start(Context ctx, int id, String quote) {
        Intent intent = new Intent(ctx, AddQuote.class);
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INPUT_ID, id);
        intent.putExtra(INPUT_QUOTE, quote);
        ctx.startActivity(intent);

        }
    }
