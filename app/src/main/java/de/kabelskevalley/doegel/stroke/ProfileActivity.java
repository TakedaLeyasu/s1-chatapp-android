package de.kabelskevalley.doegel.stroke;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.HttpChangeUserDataTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

public class ProfileActivity extends AppCompatActivity {

    private User user;
    private ImageView mImageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user =(User) StorageHelper.getInstance().getObject("user", User.class);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        toolbar.setSubtitle(user.getName());
        setSupportActionBar(toolbar);
        TextView textView = (TextView)findViewById(R.id.profile_name);
        mImageView = (ImageView)findViewById(R.id.profile_image);
        registerForContextMenu(mImageView);
        textView.setText(user.getName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(user.getThumbnail()!=null)
        {
            ImageLoader.getInstance().displayImage(user.getThumbnail(), mImageView);
        }
        Button button = (Button)findViewById(R.id.b_changeProfile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.editText_NewName)).getText().toString() ;
                if(!name.isEmpty())//Wenn EditText nicht leer ist
                {
                    user.setName(name);
                    new HttpChangeUserDataTask(mUserListener,user).execute();
                }
            }
        });
    }

    private void changeThumbnail(String uri)
    {
        user.setThumbnail(uri);
        new HttpChangeUserDataTask(mUserListener,user).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ChannelListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_profil_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sd_card:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                return true;

            case R.id.camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return true;

            case R.id.internet:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String uri = clipboard.getText().toString();
                changeThumbnail(uri);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            String uri = data.getData().toString();
            changeThumbnail(uri);
        }
    }

    private OnHttpResultListener mUserListener = new OnHttpResultListener<User>() {
        @Override
        public void onResult(User user) {
            StorageHelper.getInstance().storeObject("user",user);
            ImageView imageView = (ImageView)findViewById(R.id.profile_image);
            ImageLoader.getInstance().displayImage(user.getThumbnail(), imageView);
            TextView textView = (TextView)findViewById(R.id.profile_name);
            textView.setText(user.getName());

        }

        @Override
        public void onError(Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
            Toast.makeText(getApplicationContext(),"Daten konnten nicht geändert werden",Toast.LENGTH_SHORT).show();
        }
    };




}
