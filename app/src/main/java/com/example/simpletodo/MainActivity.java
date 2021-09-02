package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simpletodo.ItemsAdapter.OnLongClickListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //informs us that the main activity has been created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);          //sets the layout as the content

        //Define each member variable
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();
        ItemsAdapter.OnClickListener onClickListener = position -> {
            Log.d("MainActivity", "Single click at position" + position);
            //create the new activity
            //MainActivity.this refers to the current instance of MainActivity (this class)
            //EditActivity.class is referring to the class of the activity (no instance)
            // tell system this is the class we want to go to
            Intent i = new Intent(MainActivity.this, EditActivity.class);
            //pass the relevant data being edited
            i.putExtra(KEY_ITEM_TEXT, items.get(position));
            i.putExtra(KEY_ITEM_POSITION, position);
            //display the activity
            startActivityForResult(i, EDIT_TEXT_CODE);
        };
        OnLongClickListener onLongClickListener = this::onItemLongClicked;
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(v -> {
            String todoItem = etItem.getText().toString();
            //first we have to add the new item to the model
            items.add(todoItem);
            //then notify adapter that an item is inserted
            itemsAdapter.notifyItemInserted(items.size() - 1);
            //clear edit text once submitted
            etItem.setText("");
            //give the user feedback that item was added successfully: aka a TOAST
            Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
            saveItems();
        });
    }

    //Handle the result of the update/edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve the updated text value
            String itemText = null;
            if (data != null) {
                itemText = data.getStringExtra(KEY_ITEM_TEXT);
            }
            //extract teh original position of teh edited item from the position key
            int position = 0;
            if (data != null) {
                position = data.getExtras().getInt(KEY_ITEM_POSITION);
            }
            //update the model at the right position with the new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    //private bc only going to be called within MainActivity.java
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    //this func will load items by reading every line of the data file
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e){
            //logs are way to help a developer id whats actually happening
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();      //set it to empty arraylist so that we have something to build the recycler view off of
        }
    }
    //this function saves items by writing them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e){
            //logs are way to help a developer id whats actually happening
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    private void onItemLongClicked(int position) {
        //Delete the item from the model
        items.remove(position);
        //Notify the adapter at which position we deleted an item
        itemsAdapter.notifyItemRemoved(position);
        Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
    }
}