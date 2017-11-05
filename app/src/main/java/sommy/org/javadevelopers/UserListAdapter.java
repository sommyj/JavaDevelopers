package sommy.org.javadevelopers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sommy.org.javadevelopers.utilities.CircleTransform;

/**
 * This class exposes a list of github details to a the RecyclerView.
 * Created by somto on 8/25/17.
 */

class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListAdapterViewHolder> {

    private static final String TAG = UserListAdapter.class.getSimpleName();
    private final Context context;
    /**
     * An on-click handler
     */
    private final UserListOnClickHandler mClickHandler;
    private List<String> usernameList = new ArrayList<>();
    private List<String> userProfileUrlList =new ArrayList<>();
    private List <String> userProfileImageList = new ArrayList<>();

    /**
     * Creates a UserListAdapter.
     *
     * @param mClickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    UserListAdapter(Context context, UserListOnClickHandler mClickHandler) {
        this.context = context;
        this.mClickHandler = mClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType The ViewType integer is used to provide a different layout,
     *                 if the RecyclerView has more than one type of item (which ours does).
     * @return A new UserListAdapterViewHolder that holds the View for each list item
     */
    @Override
    public UserListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.user_list;
        boolean shouldAttachToparentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToparentImmediately);
        return new UserListAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the Github
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(UserListAdapterViewHolder holder, int position) {
        String usernameString = usernameList.get(position);
        String userProfileImageString = userProfileImageList.get(position);

        holder.mTextView.setText(usernameString);
        Picasso.with(context).load(userProfileImageString).transform(new CircleTransform()).into(holder.mImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our github query
     */
    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + usernameList.size());
        if(null == usernameList) {
            return 0;
        }else {
            return usernameList.size();
        }
    }

    /**
     * This method is used to set the github details on a UserListAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new UserListAdapter to display it.
     *
     * @param usernameList The new usernameList data to be displayed.
     * @param userProfileImageList The new userProfileImageList data to be displayed.
     * @param userProfileUrlList The new userProfileUrlList data to be displayed.
     */
    void setGithubJsonData(List<String> usernameList, List<String> userProfileImageList, List<String> userProfileUrlList) {
        Log.i(TAG, "setUsers() called with: users = [" + usernameList + "]");
        this.usernameList = usernameList;
        this.userProfileImageList = userProfileImageList;
        this.userProfileUrlList = userProfileUrlList;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    interface UserListOnClickHandler {
        void onClick(String[] strings);
    }

    /**
     * Cache of the children views for a username and profileImage list item.
     */
    class UserListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView mImageView;
        private final TextView mTextView;

        UserListAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.profile_imageView);
            mTextView =  itemView.findViewById(R.id.username_textView);

            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The view that was clicked
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String usernameString = usernameList.get(position);
            String userProfileImageString = userProfileImageList.get(position);
            String userProfileUrlString = userProfileUrlList.get(position);
            String[] strings = {usernameString, userProfileImageString, userProfileUrlString};
            mClickHandler.onClick(strings);
        }

    }

}
