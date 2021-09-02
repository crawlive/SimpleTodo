package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.simpletodo.R.layout.activity_edit;

public class EditActivity extends AppCompatActivity {
    EditText udtText;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_edit);

        udtText = findViewById(R.id.udtText);
        btnSave = findViewById(R.id.btnSave);

        getSupportActionBar().setTitle("Edit item");
        //whatever we pass in thats what we should pre-populate the edit text to have
        udtText.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        //when the user is done updating, they click the save button
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //create an intent which will contain the results
                Intent intent = new Intent(); //empty bc we use a shell to pass data

                //pass the data (results of editing)
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, udtText.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION)); //at what point the list should be updated

                //set the result of the intent
                setResult(RESULT_OK, intent);
                //finish the activity
                finish();
            }
        });
    }
}