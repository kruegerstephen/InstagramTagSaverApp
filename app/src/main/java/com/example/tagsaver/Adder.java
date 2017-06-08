package com.example.tagsaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Adder extends AppCompatActivity {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory);
        mEditName = (EditText)findViewById(R.id.cat_name);
        mTagsName = (EditText)findViewById(R.id.editTag);
        mAddTagButton= (Button)findViewById(R.id.addTag);
        mDoneButton = (Button)findViewById(R.id.done_button);



    }





}
