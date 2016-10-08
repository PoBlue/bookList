package com.blues.booklisting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String urlKey = "urlInputKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickBtn(View view){
        EditText inputView = (EditText) findViewById(R.id.bookNameInput);
        String inputText = inputView.getText().toString();

        if (inputText.isEmpty()){
            Toast.makeText(this, "At least one character", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, BookListActivity.class);
        intent.putExtra(urlKey,inputText);
        startActivity(intent);
    }
}
