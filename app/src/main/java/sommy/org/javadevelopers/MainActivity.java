package sommy.org.javadevelopers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sommy.org.javadevelopers.utilities.NetworkUtils;

import static sommy.org.javadevelopers.R.id.listView;

public class MainActivity extends AppCompatActivity {

    private CustomListAdapter adapter;

    private ListView mListView;
    private TextView mErrorText;

    private List<String> usernameList = new ArrayList<>();
    private List<String> userProfileUrlList =new ArrayList<>();
    private List <String> userProfileImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(listView);

        mErrorText = (TextView) findViewById(R.id.error_text);

        //If an item is clicked it launches you to the child class(ProfileActivity.java)
        // and it passes some info with the use of intent.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String usernameString = usernameList.get(position).toString();
                String userProfileImageString = userProfileImageList.get(position).toString();
                String userProfileUrlString = userProfileUrlList.get(position).toString();
                String[] strings = {usernameString, userProfileImageString, userProfileUrlString};

                Toast.makeText(getApplicationContext(), strings[0], Toast.LENGTH_SHORT).show();

                Intent startProfileActivityIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startProfileActivityIntent.putExtra(Intent.EXTRA_TEXT, strings);

                startActivity(startProfileActivityIntent);

            }
        });
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
        if(isNetworkAvailable()){
            super.onStart();
            new HttpRequestTask().execute();
        }else{
            super.onStart();
            showErrorMessage();
            Toast.makeText(MainActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This method will make the error message visible and hide the LIST
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    public void showErrorMessage(){
        mListView.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }

    /**
     * This is used to fetch for the list of Java Developers in Lagos using the Github API
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params){
            try{
                final String url = "https://api.github.com/search/users?q=location:lagos+language:java";
//                String url = NetworkUtils.buildUrl().toString();
                String s = NetworkUtils.run(url);
                return s;
            }catch (Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected  void onPostExecute(String s){
            if(null == s){
                showErrorMessage();
                Toast.makeText(MainActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();
            }else {
                //Getting the Json values of String result received.
                JSONArray items;
                String totalCount = "0";
                try {
                    JSONObject object = new JSONObject(s);
                    totalCount = object.getString("total_count");
                    items = object.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        usernameList.add(items.getJSONObject(i).getString("login"));
                        userProfileImageList.add(items.getJSONObject(i).getString("avatar_url"));
                        userProfileUrlList.add(items.getJSONObject(i).getString("html_url"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Total Count of user: "+totalCount, Toast.LENGTH_LONG).show();

                adapter = new CustomListAdapter(MainActivity.this, usernameList, userProfileImageList);
                mListView.setAdapter(adapter);
            }
        }
    }
}
