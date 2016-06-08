package de.kabelskevalley.doegel.stroke;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.network.HttpChannelTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

/**
 * An activity representing a list of Channels. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChannelDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChannelListActivity extends AppCompatActivity{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private boolean mFavourites;
    private List<Channel> favouritesList;

    private RecyclerView mRecyclerView;

    private OnHttpResultListener mChannelListener = new OnHttpResultListener<List<Channel>>() {
        @Override
        public void onResult(List<Channel> channels) {

            if(!mFavourites)
                mRecyclerView.setAdapter(new ChannelsRecyclerViewAdapter(favouritesList));
            else
                mRecyclerView.setAdapter(new ChannelsRecyclerViewAdapter(channels));

            SwipeRefreshLayout swr = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
            if (swr != null) {
                swr.setRefreshing(false);
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e("MainActivity", e.getMessage(), e);

            SwipeRefreshLayout swr = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
            if (swr != null) {
                swr.setRefreshing(false);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateChannelActivity.class);
                startActivity(intent);
            }
        });

        SwipeRefreshLayout swr = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        swr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new HttpChannelTask(mChannelListener).execute();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.channel_list);
        assert mRecyclerView != null;

        if (findViewById(R.id.channel_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        favouritesList =(List<Channel>) StorageHelper.getInstance().getObject("favourites",List.class);
        if(favouritesList == null)
            favouritesList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_channel_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return true;
        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logOut)
        {
            StorageHelper.getInstance().clear("user");
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if(item.getItemId() == R.id.profile)
        {
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.change)
        {
            if(mFavourites)
                mFavourites = false;
            else
                mFavourites = true;
            new HttpChannelTask(mChannelListener).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpChannelTask(mChannelListener).execute();
    }

    public class ChannelsRecyclerViewAdapter
            extends RecyclerView.Adapter<ChannelsRecyclerViewAdapter.ViewHolder> {

        private final List<Channel> mValues;

        public ChannelsRecyclerViewAdapter(List<Channel> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.channel_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());

            if(mValues.get(position).getThumbnail()!=null)
            {
                ImageLoader.getInstance().displayImage(mValues.get(position).getThumbnail(), holder.mImageView);
            }
            else
            {
                holder.mImageView.setImageResource(R.drawable.channel_picture);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ChannelDetailFragment.ARG_ITEM_ID, holder.mItem.getId());       //speichern von Namen und Id des Channels zur danachigen Übergabe an das Fragment
                        arguments.putString(ChannelDetailFragment.ARG_ITEM_NAME, holder.mItem.getName());
                        ChannelDetailFragment fragment = new ChannelDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.channel_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ChannelDetailActivity.class);
                        intent.putExtra(ChannelDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        intent.putExtra(ChannelDetailFragment.ARG_ITEM_NAME, holder.mItem.getName());

                        context.startActivity(intent);
                    }
                }
            });

            registerForContextMenu(holder.mView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Channel mItem;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.channel_picture);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
