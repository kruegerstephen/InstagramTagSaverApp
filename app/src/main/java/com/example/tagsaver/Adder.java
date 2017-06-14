package com.example.tagsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.TagsDBHelper;
import com.example.tagsaver.utils.InstagramUtils;
import com.example.tagsaver.utils.InstagramUtils.TagItem;
import com.example.tagsaver.utils.NetworkUtils;


//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import java.io.IOException;
//import java.net.URL;
import java.util.ArrayList;

//import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;


/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Adder extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<String> {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;
    private SQLiteDatabase mDB;
    private SQLiteDatabase mDataBaseToCheck;
    private String tags = "";
    private Context context;
    final static private String[] tagAndNumber = new String[2];


    private static final String INSTAGRAM_URL_KEY = "instagramUrl";
    private static final int INSTAGRAM_LOADER_ID = 0;
    private InstagramUtils instagramUtil;


    private RecyclerView mCatListRecyclerView;
    private adderRecyclerAdapter mTagAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        CharSequence text = "Hello toast!";
        final Toast[] toast = new Toast[1];
        //new ApiRequest().execute();

        setContentView(R.layout.addcategory);

        // Defining activity elements
        mEditName = (EditText) findViewById(R.id.cat_name);
        mTagsName = (EditText) findViewById(R.id.editTag);
        mAddTagButton = (Button) findViewById(R.id.addTag);
        mDoneButton = (Button) findViewById(R.id.done_button);

        // Instagram
        instagramUtil = new InstagramUtils();

        // Database setup
        TagsDBHelper dbHelper = new TagsDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
        mDataBaseToCheck = dbHelper.getReadableDatabase();

        // When done save tags
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean alreadyThere = false;
                Cursor cursor = mDataBaseToCheck.query(
                        CategoriesContract.FavoriteRepos.TABLE_NAME, // The table to query
                        null,                               // The columns to return
                        null,                               // The columns for the WHERE clause
                        null,                               // The values for the WHERE clause
                        null,                               // don't group the rows
                        null,                               // don't filter by row groups
                        null                           // The sort order
                );
                while (cursor.moveToNext()) {
                    String value = cursor.getString(cursor.getColumnIndex(CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME));
                    if (value.equals(mEditName.getText().toString())) {
                        alreadyThere = true;
                    }
                }
                // Code here executes on main thread after user presses button
                if (!alreadyThere) {
                    createNewCategory();
                    Log.d(TAG, "write new category" + tags);
                } else {
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
        mCatListRecyclerView = (RecyclerView) findViewById(R.id.rv_tags_list);
        mCatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCatListRecyclerView.setHasFixedSize(true);
        mTagAdapter = new adderRecyclerAdapter();
        mCatListRecyclerView.setAdapter(mTagAdapter);
        mAddTagButton.setOnClickListener(this);


    }

    private long createNewCategory() {

        if (mEditName.getText() != null) {
            ContentValues values = new ContentValues();

            values.put(CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME,
                    mEditName.getText().toString());
            values.put(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION, tags);
            return mDB.insert(CategoriesContract.FavoriteRepos.TABLE_NAME, null, values);

        } else {
            return -1;
        }
    }


    @Override
    public void onClick(View v) {

        String tagText = mTagsName.getText().toString();

        String instagramURL = instagramUtil.buildTagURL(tagText);

        Log.d("Adder", "Text has been clicked");
        Log.d("Adder", tagText);
        Log.d("Adder", instagramURL);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(INSTAGRAM_URL_KEY, instagramURL);
        getSupportLoaderManager().initLoader(INSTAGRAM_LOADER_ID, argsBundle, this);



//        if (!tagText.equals("")) {
//            tags += " " + tagText;
//            if (!TextUtils.isEmpty(tagText)) {
//                new ApiRequest().execute();
//                mTagAdapter.addTag(tagText);
//
//                mTagsName.setText("");
//            }
//        }

    }

//    private class ApiRequest extends AsyncTask<URL, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        private OkHttpClient mHTTPClient = new OkHttpClient();
//        private InstagramUtils iUtils = new InstagramUtils();
//
//        //String tagInEdit=mTagsName.getText().toString();
//
//        @Override
//        protected String doInBackground(URL... params) {
//            String requestResult = null;
//            Response response = null;
//            String url = "https://api.instagram.com/v1/tags/"+"snow"+"?access_token=328437615.0d2ac8b.112957f8ed8847f2a6e9cd799fdd9e42"; //add access token here
//            //String url = iUtils.buildTagURL()
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            try {
//                response = mHTTPClient.newCall(request).execute();
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            try {
//                JSONObject myJson = new JSONObject(s);
//                JSONObject data = myJson.getJSONObject("data");
//                //tagAndNumber[0]= data.getString("name");
//                //tagAndNumber[1] = Integer.toString(data.getInt("media_count"));
//                //mTagAdapter.addTag(" #"+tagAndNumber[0]+" n"+tagAndNumber[1]);
//                Log.d(TAG, data.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mInstagramJSON;

            @Override
            protected void onStartLoading() {
                if (mInstagramJSON != null) {
                    Log.d(TAG, "AsyncTaskLoader delivering cached Instagram");
                    deliverResult(mInstagramJSON);
                } else {
                    //mLoadingIndicatorPB.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String InstagramURL = args.getString(INSTAGRAM_URL_KEY);

                if (InstagramURL == null || InstagramURL.equals("")) {
                    return null;
                }

                Log.d(TAG, "AsyncTaskLoader loading Instagram from url: " + InstagramURL);

                String InstagramJSON = null;

                try {
                    InstagramJSON = NetworkUtils.doHTTPGet(InstagramURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return InstagramJSON;
            }

            @Override
            public void deliverResult(String InstagramJSON) {
                mInstagramJSON = InstagramJSON;
                super.deliverResult(InstagramJSON);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String InstagramJSON) {
        Log.d(TAG, "AsyncTaskLoader load finished");
        TagItem tag = instagramUtil.parseTagJSON(InstagramJSON);
        mTagAdapter.addTag(tag);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do here...
    }
}
