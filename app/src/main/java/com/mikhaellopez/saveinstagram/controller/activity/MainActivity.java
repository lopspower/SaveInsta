package com.mikhaellopez.saveinstagram.controller.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.saveinstagram.R;
import com.mikhaellopez.saveinstagram.controller.activity.generic.ABaseActivity;
import com.mikhaellopez.saveinstagram.controller.model.EventInstaPictureLoad;
import com.mikhaellopez.saveinstagram.controller.model.InstaData;
import com.mikhaellopez.saveinstagram.controller.model.InstaOwner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends ABaseActivity {

    @Bind(R.id.card_view)
    protected View mCardView;
    @Bind(R.id.layout_input)
    protected View mLayoutInput;
    @Bind(R.id.progressBar)
    protected View mProgressBar;
    @Bind(R.id.image_to_download)
    protected ImageView mImageToDownload;
    @Bind(R.id.icon_profile)
    protected ImageView mIconProfil;
    @Bind(R.id.text_user_name)
    protected TextView mTextUserName;
    @Bind(R.id.edit_insta_url)
    protected EditText editInstaUrl;

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Past Clipboard
        String instaUrl = pastClipboard();
        // Init View
        initView(instaUrl);
    }

    private void initView(String instaUrl) {
        // Init View
        mCardView.setVisibility(View.GONE);
        mLayoutInput.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        // Check Insta URL
        if (instaUrl != null && instaUrl.contains("https://www.instagram.com/p/")) {
            // Download Image
            downloadImageData(instaUrl);
        } else {
            // Show Input
            mProgressBar.setVisibility(View.GONE);
            mLayoutInput.setVisibility(View.VISIBLE);
        }
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

    @OnClick(R.id.text_user_name)
    protected void onClickUserName() {
        Uri uri = Uri.parse("http://instagram.com/_u/" + mUserName);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + mUserName)));
        }
    }

    @OnClick(R.id.image_action_close)
    protected void onClickClose() {
        // Clear Clipboard
        ClipboardManager clipService = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", "");
        clipService.setPrimaryClip(clipData);
        // Init View
        initView(null);
    }

    @OnClick(R.id.btn_open_insta)
    protected void onClickOpenInstagram() {
        String packageName = "com.instagram.android";
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_save_it)
    protected void onClickSaveIt() {
        // Init View
        initView(editInstaUrl.getText().toString());
    }

    private void downloadImageData(final String instaUrl) {
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

                    String startJson = "<script type=\"text/javascript\">window._sharedData = ";
                    String endJson = ";</script>";

                    String json = html.substring(html.indexOf(startJson)+startJson.length(),
                            html.indexOf(endJson));

                    InstaData instaData = new Gson().fromJson(json, InstaData.class);

                    InstaOwner instaOwner = instaData.getEntry_data().getPostPage().get(0).getMedia().getOwner();
                    String userName = instaOwner.getUsername();
                    String fullName = instaOwner.getFull_name();
                    String urlProfile = instaOwner.getProfile_pic_url();

                    EventBus.getDefault().post(new EventInstaPictureLoad(userName, fullName, instaUrl + "media/?size=l", urlProfile));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }).start();
    }

    public void onEventMainThread(final EventInstaPictureLoad eventInstaPictureLoad) {
        // Save userName
        mUserName = eventInstaPictureLoad.getUserName();

        // Load User Name to View
        mTextUserName.setText(eventInstaPictureLoad.getUserFullName());

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
