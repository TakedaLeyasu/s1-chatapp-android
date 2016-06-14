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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.network.HttpChannelTask;
import de.kabelskevalley.doegel.stroke.network.HttpDeleteChannelTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

/**
 * An activity representing a list of Channels. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChannelDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChannelListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private boolean mFavourites;
    private List<Channel> mChannelList;
    private List<Channel> mFavouritesList;

    private RecyclerView mRecyclerView;
    private ChannelsRecyclerViewAdapter mAdapter;

    private OnHttpResultListener mDeleteListener = new OnHttpResultListener<Boolean>() {
        @Override
        public void onResult(Boolean result) {
        }

        @Override
        public void onError(Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
    };

    private OnHttpResultListener mChannelListener = new OnHttpResultListener<List<Channel>>() {
        @Override
        public void onResult(List<Channel> channels) {
            mChannelList = channels;
            if (mFavourites)
                mAdapter = new  ChannelsRecyclerViewAdapter(mFavouritesList);
            else
                mAdapter = new  ChannelsRecyclerViewAdapter(channels);
            mRecyclerView.setAdapter(mAdapter);

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
        registerForContextMenu(mRecyclerView);
        mAdapter = new ChannelsRecyclerViewAdapter(mChannelList);

        if (findViewById(R.id.channel_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_channel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                StorageHelper.getInstance().clear("user");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.profile:
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.change:
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                if (mFavourites) {
                    mFavourites = false;
                    toolbar.setTitle("Stroke!");
                } else {
                    mFavourites = true;
                    toolbar.setTitle("Stroke!   -   Favoriten");
                }
                StorageHelper.getInstance().storeObject("mFavourites",mFavourites);
                new HttpChannelTask(mChannelListener).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {mFavourites = (Boolean) StorageHelper.getInstance().getObject("mFavourites", Boolean.class);}
        catch (Exception e ){mFavourites = false;}
        if(mFavourites)
        {
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            toolbar.setTitle("Stroke!   -   Favoriten");
        }
        //TODO
        /*
        mFavouritesList = (List<Channel>) StorageHelper.getInstance().getObject("favourites", ArrayList.class);
        if (mFavouritesList == null)
            mFavouritesList = new ArrayList<>();
        */
        mFavouritesList = new ArrayList<>();
        new HttpChannelTask(mChannelListener).execute();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (mFavourites) {
                    new HttpDeleteChannelTask(mDeleteListener, mFavouritesList.get(mAdapter.getPosition())).execute();
                    mFavouritesList.remove(mAdapter.getPosition());
                } else {
                    new HttpDeleteChannelTask(mDeleteListener, mChannelList.get(mAdapter.getPosition())).execute();
                    if(mFavouritesList.contains(mChannelList.get(mAdapter.getPosition())))
                        mFavouritesList.remove(mAdapter.getPosition());
                }
                return true;

            case R.id.show_id:
                if (mFavourites) {
                    Toast.makeText(getApplicationContext(),mFavouritesList.get(mAdapter.getPosition()).getId(),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),mChannelList.get(mAdapter.getPosition()).getId(),Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.favo:
                if (mFavourites) {
                    mFavouritesList.remove(mAdapter.getPosition());
                    Toast.makeText(getApplicationContext(), "Kanal von Favoriten gelöscht", Toast.LENGTH_SHORT).show();
                    new HttpChannelTask(mChannelListener).execute();
                } else {
                    Channel channel = mChannelList.get(mAdapter.getPosition());
                    mFavouritesList.add(channel);
                    Toast.makeText(getApplicationContext(), "Kanal zu Favoriten hinzugefügt", Toast.LENGTH_SHORT).show();
                }
                StorageHelper.getInstance().storeObject("favourites",mFavouritesList);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public class ChannelsRecyclerViewAdapter
            extends RecyclerView.Adapter<ChannelsRecyclerViewAdapter.ViewHolder> {

        private final List<Channel> mValues;
        private int position;
        public int getPosition() {return position;}
        private void setPosition(int position) { this.position = position;}

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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());

            if (mValues.get(position).getThumbnail() != null
                    && mValues.get(position).getThumbnail().trim() != "") {
                ImageLoader.getInstance().displayImage(mValues.get(position).getThumbnail(), holder.mImageView);
            } else {
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

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(position);
                    return false;
                }
            });
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
