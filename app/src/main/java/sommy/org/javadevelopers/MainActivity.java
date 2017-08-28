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

    private UserListAdapter userListAdapter;

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

        userListAdapter = new UserListAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(userListAdapter);

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
        if(isNetworkAvailable()){
            super.onStart();
            getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
        }else{
            super.onStart();
            showErrorMessage();
            Toast.makeText(MainActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();
        }

    }

    protected void onStop(){
        super.onStop();
        usernameList.clear();
        userProfileImageList.clear();
        userProfileUrlList.clear();

    }

    /**
     * This method will make the error message visible and hide the ListView.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    public void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
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
            userListAdapter.setGithubJsonData(usernameList, userProfileImageList, userProfileUrlList );
            Toast.makeText(MainActivity.this, usernameList.size()+" Java Developers in Lagos", Toast.LENGTH_SHORT ).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
