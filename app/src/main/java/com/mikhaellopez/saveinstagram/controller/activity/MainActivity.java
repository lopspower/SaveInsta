package com.mikhaellopez.saveinstagram.controller.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.mikhaellopez.saveinstagram.R;
import com.mikhaellopez.saveinstagram.controller.activity.generic.ABaseActivity;
import com.mikhaellopez.saveinstagram.controller.model.EventInstaPictureLoad;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class MainActivity extends ABaseActivity {

    @Bind(R.id.card_view)
    protected CardView mCardView;
    @Bind(R.id.progressBar)
    protected ProgressBar mProgressBar;
    @Bind(R.id.image_to_download)
    protected ImageView mImageToDownload;
    @Bind(R.id.icon_profile)
    protected CircularImageView mIconProfil;
    @Bind(R.id.text_user_name)
    protected TextView mTextUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        String instaUrl = pastClipboard();
        Log.e("Mikhael", instaUrl + "media/?size=l");
        downloadImageData(instaUrl);
    }

    private String pastClipboard() {
        String textToPaste = null;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                textToPaste = clip.getItemAt(0).getText().toString();
            }
        }
        return textToPaste;
    }

    private void downloadImageData(final String instaUrl) {
        if (instaUrl != null && instaUrl.contains("https://www.instagram.com/p/")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Get User and Profil Picture
                    try {
                        URL url = new URL(instaUrl);
                        InputStream is = url.openStream();
                        int ptr;
                        StringBuffer buffer = new StringBuffer();
                        while ((ptr = is.read()) != -1) {
                            buffer.append((char) ptr);
                        }

                        String html = buffer.toString();

                        String userNameStartSeparator = "\"user\":{\"username\":\"";
                        String userNameEndSeparator = "\",\"profile_pic_url\":\"";
                        int indexOfUserNameSeparator = html.indexOf(userNameEndSeparator);
                        String userPictureEndSeparator = "\",\"id\":\"";

                        String userName = html.substring(html.indexOf(userNameStartSeparator) + userNameStartSeparator.length(),
                                indexOfUserNameSeparator);
                        String urlProfile = html.substring(indexOfUserNameSeparator + userNameEndSeparator.length(),
                                html.indexOf(userPictureEndSeparator)).replaceAll("\\\\", "");

                        Log.e("Mikhael", "User : " + userName);
                        Log.e("Mikhael", "urlProfile : " + urlProfile);

                        EventBus.getDefault().post(new EventInstaPictureLoad(instaUrl + "media/?size=l", urlProfile, userName));

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public void onEventMainThread(final EventInstaPictureLoad eventInstaPictureLoad) {
        // Load User Name to View
        mTextUserName.setText(eventInstaPictureLoad.getProfilName());

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Load Image to View
                        Picasso.with(MainActivity.this).load(eventInstaPictureLoad.getUrlImage())
                                .into(mImageToDownload, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Show Result
                                        mProgressBar.setVisibility(View.GONE);
                                        mCardView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });

                        // Load Profil Icon to View
                        Picasso.with(MainActivity.this).load(eventInstaPictureLoad.getUrlIconProfil())
                                .into(mIconProfil);
                    }
                });
            }
        }).start();
    }

    //region Menu Implememtatiom
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion


}
