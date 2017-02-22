package com.codepath.note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
MainActivity is the entry activity. It shows a list with notes. On a long click at a note the user can remove it.
On a short click the user can edit an existing node. This is done by calling EditItemActivity and passing the note text and position.
If the user wants to add a new note he can fill the EditText field and save the new note.
 */
public class MainActivity extends AppCompatActivity
{
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    private final String TAG = "MAINACTIVITY";
    private final int REQUEST_CODE = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onAddItem();
            }
        });

        setupListViewListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check if it is our request and the result is ok
        if( requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // get the new note text and the actual note position
            String text = data.getExtras().getString("noteText");
            int position = data.getExtras().getInt("notePosition");
            updateNote(text, position);
            // show a notification to the user that the action was successful
            Toast.makeText(MainActivity.this, "Edit successful", Toast.LENGTH_SHORT).show();


        }
    }
    // used to update a note after it is edited
    private void updateNote(String text, int position)
    {
        if(position != -1)
        {
            items.set(position, text);
            // notify the adapter that the notes changed
            itemsAdapter.notifyDataSetChanged();
            // write the notes to storage after the update
            writeItems();
        }
        else
        {
            Log.e(TAG,"Something messed up in the ListView or while putting the arguments on the intent");
        }

    }
    // set the long and short click listener for the ListView
    private void setupListViewListener()
    {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int position, long id)
            {
                items.remove(position);
                // notify the adapter that the notes changed
                itemsAdapter.notifyDataSetChanged();
                // rewrite the remaining notes
                writeItems();
                return true;
            }

        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int position, long id)
            {
                String text = items.get(position);
                launchEditForm(text, position);
            }

        });
    }
    // get the notes from our text file
    private void readItems()
    {
        File filesDir = getFilesDir();
        File noteFile = new File(filesDir, "notes.txt");
        try
        {
            items = new ArrayList<>(FileUtils.readLines(noteFile));
        }
        catch(IOException e)
        {
            Log.d(TAG, "Starting with an empty list of notes since we have error: " + e.toString());
            items = new ArrayList<>();
        }

    }
    // launch the form to edit the note and pass the note body and position
    private void launchEditForm(String text, int position)
    {
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("noteText", text);
        intent.putExtra("notePosition", position);
        startActivityForResult(intent, REQUEST_CODE);
    }
    // write the notes to our text file, so that the data can persist after closing the app.
    private void writeItems()
    {
        File filesDir = getFilesDir();
        File noteFile = new File(filesDir, "notes.txt");
        try
        {
            FileUtils.writeLines(noteFile, items);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Cannot write our notes to permanent storage: " + e.toString());
        }

    }
    // add a note to our note list and write the list to storage.
    private void onAddItem()
    {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String note = etNewItem.getText().toString();
        // check if there is input in the EditText
        if(note.length() > 0)
        {
            itemsAdapter.add(note);
            // clear the Edit Text
            etNewItem.setText("");
            // write the newly added note to storage
            writeItems();
        }
        else
        {
            Log.d(TAG, "No actual input to the EditText Field");
        }

    }
}
