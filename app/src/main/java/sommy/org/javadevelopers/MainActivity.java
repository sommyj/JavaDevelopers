package sommy.org.javadevelopers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    List<String> list = new ArrayList<>();

    Integer[] imgid= {
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= list.get(position).toString();
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activenetworkInfo != null && activenetworkInfo.isConnected();
    }

    protected void onStart(){
        if(isNetworkAvailable()){
            super.onStart();
            new HttpRequestTask().execute();
        }else{
            super.onStart();
            setContentView(R.layout.activity_main);
            Toast.makeText(this,"please connect your device",Toast.LENGTH_SHORT).show();
        }

    }

    private class HttpRequestTask extends AsyncTask<Void, Void, JavaDevelopers>{

        @Override
        protected JavaDevelopers doInBackground(Void... params){
            try{
                final String url = "https://api.github.com/search/users?q=location:lagos+language:java";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                JavaDevelopers javaDevelopers = restTemplate.getForObject(url, JavaDevelopers.class);
                return javaDevelopers;
            }catch (Exception e){
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected  void onPostExecute(JavaDevelopers javaDevelopers){
            Toast.makeText(MainActivity.this,String.valueOf(javaDevelopers.getTotal_count()),Toast.LENGTH_SHORT).show();

            for(Items items:javaDevelopers.getItems()){
               list.add(items.getUrl());
            }

            list.add(String.valueOf(javaDevelopers.getTotal_count()));
            list.add(String.valueOf(javaDevelopers.isIncomplete_results()));
            CustomListAdapter adapter=new CustomListAdapter(MainActivity.this, list, imgid);
            lv.setAdapter(adapter);
        }
    }
}
