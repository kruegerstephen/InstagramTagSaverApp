package com.example.tagsaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

// Instagram
import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.Instagram;
import com.example.tagsaver.utils.Instagram.OAuthAuthenticationListener;
import com.example.tagsaver.utils.ApplicationData;
import com.example.tagsaver.utils.TagsDBHelper;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private TextView mLoadingErrorMessageTV;
    private boolean isErrorVisible= false;
    private RecyclerAdapter mCategoriesAdapter;
    private SQLiteDatabase mDBread;
    private SQLiteDatabase mDBwrite;

    private String[] whereCondition = {""};
    private static final String TAG = MainActivity.class.getSimpleName();




    // Instagram stuff
    private Instagram mApp;
    private Button btnConnect, btnViewInfo, btnGetAllImages, btnFollowers,
            btnFollwing;
    private LinearLayout llAfterLoginView;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Instagram.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == Instagram.WHAT_FINALIZE) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLoadingErrorMessageTV=(TextView)findViewById(R.id.tv_loading_error_message);
        TagsDBHelper dbHelper = new TagsDBHelper(this);
        mDBread=dbHelper.getReadableDatabase();
        String sortOrder =
                CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME+ " DESC";
        String[] proj={CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME,CategoriesContract.FavoriteRepos._ID};
        String whereClauseArgs = "";

        Cursor cursor = mDBread.query(
                CategoriesContract.FavoriteRepos.TABLE_NAME, // The table to query
                null,                                // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //TEST ARE BELOW THIS COMMENT LINE
        Log.d(TAG,"cursor created");
        cursor.moveToNext();
        cursor.moveToNext();
        cursor.moveToNext();
        Long itemId = new Long(cursor.getLong(cursor.getColumnIndexOrThrow(CategoriesContract.FavoriteRepos._ID)));
        String value = cursor.getString(cursor.getColumnIndex(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION));
        Log.d(TAG,itemId.toString());
        Log.d(TAG,value);
        //TESTS ARE ABOVE THIS COMMENT

        //WE NEED TO REMOVE THAT BECAUSE WE NEED THE FIRST ITEM IN THE RECYCLER VIEW



        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearchBoxET, 0);
//        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(mSearchBoxET, InputMethodManager.SHOW_IMPLICIT);

        // Instagram Intent
        mApp = new Instagram(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                // tvSummary.setText("Connected as " + mApp.getUserName());
                btnConnect.setText("Disconnect");
                //llAfterLoginView.setVisibility(View.VISIBLE);
                // userInfoHashmap = mApp.
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Adder.class);
                startActivity(intent);
            }
        });

        // click events
        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectOrDisconnectUser();
            }
        });

        if (mApp.hasAccessToken()) {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
            //llAfterLoginView.setVisibility(View.VISIBLE);
            mApp.fetchUserName(handler);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    // btnConnect.setVisibility(View.VISIBLE);
                                    //llAfterLoginView.setVisibility(View.GONE);
                                    btnConnect.setText("Connect");
                                    // tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }
}
