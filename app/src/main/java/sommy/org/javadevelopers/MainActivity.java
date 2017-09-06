package sommy.org.javadevelopers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sommy.org.javadevelopers.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements UserListAdapter.UserListOnClickHandler, LoaderManager.LoaderCallbacks<String>{

    private static final int GITHUB_SEARCH_LOADER = 22;

    private UserListAdapter mUserListAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mPbIndicator;

    private List<String> usernameList = new ArrayList<>();
    private List<String> userProfileUrlList =new ArrayList<>();
    private List <String> userProfileImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mErrorTextView = (TextView) findViewById(R.id.error_textView);

        mPbIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mUserListAdapter = new UserListAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mUserListAdapter);

    }

    /**
     * This method is use to check for active network on the device.
     * @return a boolean variable ture or false if network is avalable on the device.
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activenetworkInfo != null && activenetworkInfo.isConnected();
    }

    /**
     * This method start the content view if there is active network or send and error message if not.
     */
    protected void onStart(){
        super.onStart();
        if(usernameList.isEmpty() && userProfileUrlList.isEmpty() && userProfileImageList.isEmpty()){
            getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
        }

    }

    protected void onStop(){
        super.onStop();
        invalidateData();
    }


    /**
     * This method will make the error message visible and hide the ListView.
     */
    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the github data visible and
     * hide the error message.
     */
    private void showGithubDataView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * If an item is clicked it launches you to the child class(ProfileActivity.java).
     * and it passes some info with the use of intent.
     * @param strings The Github data to be displayed.
     */
    @Override
    public void onClick(String[] strings){
        Intent startProfileActivityIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startProfileActivityIntent.putExtra(Intent.EXTRA_TEXT, strings);

                startActivity(startProfileActivityIntent);
    }

    /**
     * This method is used to fetch for the list of Java Developers in Lagos using the Github API.
     * @param id The LoaderManager an ID
     * @param args The bundle that will receive data from initialize loader.
     * @return An AsyncTaskLoader that will return the Loader to the onLoadFinished method.
     */
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mGithubJson;

            @Override
            protected void onStartLoading(){

                mPbIndicator.setVisibility(View.VISIBLE);
                if(mGithubJson != null){
                    deliverResult(mGithubJson);
                }else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                try {
                    final String url = "https://api.github.com/search/users?q=location:lagos+language:java";
//                String url = NetworkUtils.buildUrl().toString();
                    return NetworkUtils.run(url);
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }
                return null;
            }

            @Override
            public void deliverResult(String githubJson) {
                mGithubJson = githubJson;
                super.deliverResult(githubJson);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        mPbIndicator.setVisibility(View.INVISIBLE);
        if (null == data) {
            showErrorMessage();
            Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
        } else {
            //Getting the Json values of String result received.
            JSONArray items;
            JSONObject object;
            try {
                object = new JSONObject(data);
                items = object.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    usernameList.add(items.getJSONObject(i).getString("login"));
                    userProfileImageList.add(items.getJSONObject(i).getString("avatar_url"));
                    userProfileUrlList.add(items.getJSONObject(i).getString("html_url"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mUserListAdapter.setGithubJsonData(usernameList, userProfileImageList, userProfileUrlList );
            Toast.makeText(MainActivity.this, usernameList.size()+" Java Developers in Lagos", Toast.LENGTH_SHORT ).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        usernameList.clear();
        userProfileImageList.clear();
        userProfileUrlList.clear();
        mUserListAdapter.setGithubJsonData(usernameList, userProfileImageList, userProfileUrlList );
        showGithubDataView();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasSelected = menuItem.getItemId();
        if(itemThatWasSelected == R.id.action_refresh) {
            invalidateData();
            getSupportLoaderManager().restartLoader(GITHUB_SEARCH_LOADER, null, this);

        }
            return super.onOptionsItemSelected(menuItem);
    }
}
