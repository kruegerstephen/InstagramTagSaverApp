package com.example.tagsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.TagsDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Adder extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;
    private SQLiteDatabase mDB;
    private SQLiteDatabase mDataBaseToCheck;
    private String tags="";
    private Context context;
    final static private String[] tagAndNumber=new String[2];


    private RecyclerView mCatListRecyclerView;
    private adderRecyclerAdapter mTagAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        CharSequence text = "Hello toast!";
        final Toast[] toast = new Toast[1];

        setContentView(R.layout.addcategory);
        mEditName = (EditText)findViewById(R.id.cat_name);
        mTagsName = (EditText)findViewById(R.id.editTag);
        mAddTagButton= (Button)findViewById(R.id.addTag);
        mDoneButton = (Button)findViewById(R.id.done_button);
        TagsDBHelper dbHelper = new TagsDBHelper(this);
        mDB=dbHelper.getWritableDatabase();
        mDataBaseToCheck=dbHelper.getReadableDatabase();
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean alreadyThere=false;
                Cursor cursor = mDataBaseToCheck.query(
                        CategoriesContract.FavoriteRepos.TABLE_NAME, // The table to query
                        null,                               // The columns to return
                        null,                               // The columns for the WHERE clause
                        null,                               // The values for the WHERE clause
                        null,                               // don't group the rows
                        null,                               // don't filter by row groups
                        null                           // The sort order
                );
                while (cursor.moveToNext()){
                    String value = cursor.getString(cursor.getColumnIndex(CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME));
                    if (value.equals(mEditName.getText().toString())){
                        alreadyThere=true;
                    }
                }
                // Code here executes on main thread after user presses button
                if (!alreadyThere) {
                    createNewCategory();
                    Log.d(TAG, "write new category" + tags);
                }else{
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence text = "You can't use this name because it is already used";
                    if (toast[0] == null) {
                        toast[0] = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    }
                    toast[0].setText(text);
                    toast[0].show();

                    //make toast to say that they can't use this name
                }
            }
        });

        //recycler adapter
        mCatListRecyclerView = (RecyclerView)findViewById(R.id.rv_tags_list);
        mCatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCatListRecyclerView.setHasFixedSize(true);
        mTagAdapter = new adderRecyclerAdapter();
        mCatListRecyclerView.setAdapter(mTagAdapter);
        mAddTagButton.setOnClickListener(this);


    }

    private long createNewCategory(){

        if (mEditName.getText() != null) {
            ContentValues values = new ContentValues();

            values.put(CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME,
                    mEditName.getText().toString());
            values.put(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION, tags);
            return mDB.insert(CategoriesContract.FavoriteRepos.TABLE_NAME, null, values);

        }else{
            return -1;
        }
    }






    @Override
    public void onClick(View v) {
        String tagText = mTagsName.getText().toString();
        if (!tagText.equals("")) {
            tags += " " + tagText;
            if (!TextUtils.isEmpty(tagText)) {
                mTagAdapter.addTag(tagText);

                mTagsName.setText("");
            }
        }

    }

    private class ApiRequest extends AsyncTask<URL,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        private OkHttpClient mHTTPClient = new OkHttpClient();

        String tagInEdit=mTagsName.getText().toString();

        @Override
        protected String doInBackground(URL... params) {
            String requestResult = null;
            Response response = null;
            String url = "https://api.instagram.com/v1/tags/"+tagInEdit+"?"; //add access token here
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                response = mHTTPClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject myJson = new JSONObject(s);
                JSONObject data=myJson.getJSONObject("data");
                tagAndNumber[0]= data.getString("name");
                tagAndNumber[1] = Integer.toString(data.getInt("media_count"));
                mTagAdapter.addTag(" #"+tagAndNumber[0]+" n"+tagAndNumber[1]);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
}
