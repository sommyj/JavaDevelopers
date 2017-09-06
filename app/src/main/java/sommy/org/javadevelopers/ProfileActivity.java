package sommy.org.javadevelopers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sommy.org.javadevelopers.utilities.RoundedCornersTransform;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImageView;
    private TextView mUsernameTextView;
    private TextView mProfileUrlTextView;

    private String usernameTextEntered;
    private String userProfileImageTextEntered;
    private String userProfileUrlTextEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProfileImageView = (ImageView) findViewById(R.id.profile_imageView2);
        mUsernameTextView = (TextView) findViewById(R.id.username_textView2);
        mProfileUrlTextView = (TextView) findViewById(R.id.profile_url_textView);

        /*
          This intent retrieves the username, github profile Url and profile image url from the
          parent class(MainActivity.java) in an array of String.
         */
        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            String[] strings = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
            usernameTextEntered = strings[0];
            userProfileImageTextEntered = strings[1];
            userProfileUrlTextEntered = strings[2];
            mUsernameTextView.setText(usernameTextEntered);
            mProfileUrlTextView.setText(userProfileUrlTextEntered);
            Picasso.with(this).load(userProfileImageTextEntered).transform(new RoundedCornersTransform()).into(mProfileImageView);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int itemThatWasSelected = item.getItemId();
        if(itemThatWasSelected == R.id.action_share){
            String textToShare = "Check out this awesome developer @"+usernameTextEntered+", "+userProfileUrlTextEntered+".";
            String mimeType = "text/plain";
            String title = "";
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setText(textToShare)
                    .startChooser();
        }

        // When the home button is pressed, take the user back to the VisualizerActivity
        if (itemThatWasSelected == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
