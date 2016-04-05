package de.kabelskevalley.doegel.stroke;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

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
    private boolean delete = false;

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
                create_ChannelItem();
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_bool();
            }
        });

        View recyclerView = findViewById(R.id.channel_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.channel_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    private void change_bool()
    {
        FrameLayout layout = (FrameLayout)findViewById(R.id.frameLayout);
        if(delete == false)
        {
            delete = true;
            layout.setBackgroundColor(Color.RED);
        }
        else
        {
            delete = false;
            layout.setBackgroundColor(Color.WHITE);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ChannelContent.ITEMS));
    }

    private void create_ChannelItem() {
        Intent intent = new Intent(this, NewChannelActivity.class);
        startActivity(intent);

    }

        public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {



        private final List<ChannelContent.ChannelItem> mValues;
        public SimpleItemRecyclerViewAdapter(List<ChannelContent.ChannelItem> items) {
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

                holder.mContentView.setText(mValues.get(position).content);



                holder.mView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if(delete == false) {
                                                            if (mTwoPane) {
                                                                Bundle arguments = new Bundle();
                                                                arguments.putString(ChannelDetailFragment.ARG_ITEM_ID, holder.mItem.content);
                                                                ChannelDetailFragment fragment = new ChannelDetailFragment();
                                                                fragment.setArguments(arguments);
                                                                getSupportFragmentManager().beginTransaction()
                                                                        .replace(R.id.channel_detail_container, fragment)
                                                                        .commit();
                                                            } else {
                                                                Context context = v.getContext();
                                                                Intent intent = new Intent(context, ChannelDetailActivity.class);
                                                                intent.putExtra(ChannelDetailFragment.ARG_ITEM_ID, holder.mItem.content);

                                                                context.startActivity(intent);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            if(holder.mItem.id != 0) {
                                                                ChannelContent.deleteItem(holder.mItem.id);
                                                                View recyclerView = findViewById(R.id.channel_list);
                                                                assert recyclerView != null;
                                                                setupRecyclerView((RecyclerView) recyclerView);
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(ChannelListActivity.this,"Dieser Kanal ist nicht löschbar",Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(ChannelListActivity.this,"Muhahaha!!! :D",Toast.LENGTH_SHORT).show();        //Unbedingt notwendig!
                                                            }
                                                            delete = false;
                                                            FrameLayout layout = (FrameLayout)findViewById(R.id.frameLayout);
                                                            layout.setBackgroundColor(Color.TRANSPARENT);
                                                        }
                                                    }
                                                }
                );
                holder.mView.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v)
                    {
                        Toast.makeText(ChannelListActivity.this, holder.mItem.details, Toast.LENGTH_LONG).show();
                        return true;
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
            public final ImageView mImageView;
            public ChannelContent.ChannelItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.imageView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
