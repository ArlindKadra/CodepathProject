package com.codepath.note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditItemActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        // get the intent, so we can access the data
        Intent data = getIntent();
        // get the note text
        String text = data.getStringExtra("noteText");
        // get the note position
        final int position = data.getIntExtra("notePosition", -1);
        setupForm(text);

        Button btnSave = (Button) findViewById(R.id.btnSaveChanges);
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(validateNoteText())
                {
                    returnResults(getNoteText(), position);
                }
            }
        });
    }

    private void returnResults(String text, int position)
    {
        Intent result = new Intent();
        result.putExtra("noteText", text);
        result.putExtra("notePosition", position);
        setResult(RESULT_OK, result);
        this.finish();
    }

    private void setupForm(String text)
    {
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(text);
        int cursorPosition = text.length();
        etEditItem.setSelection(cursorPosition);
        etEditItem.requestFocus();
    }

    private boolean validateNoteText()
    {
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        String text = etEditItem.getText().toString();
        if(text.length() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private String getNoteText()
    {
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        return etEditItem.getText().toString();
    }
}
