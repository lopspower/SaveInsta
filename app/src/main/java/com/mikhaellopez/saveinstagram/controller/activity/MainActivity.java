package com.mikhaellopez.saveinstagram.controller.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.saveinstagram.R;
import com.mikhaellopez.saveinstagram.controller.activity.generic.ABaseActivity;
import com.mikhaellopez.saveinstagram.controller.model.EventInstaPictureLoad;
import com.mikhaellopez.saveinstagram.controller.model.InstaData;
import com.mikhaellopez.saveinstagram.controller.model.InstaMedia;
import com.mikhaellopez.saveinstagram.controller.model.InstaOwner;
import com.mikhaellopez.saveinstagram.controller.utils.PermissionsUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends ABaseActivity {

    @Bind(R.id.coordinator_layout)
    protected View mCoordinatorLayout;
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
    private String mIdImage;
    private String mCurrentUrlRemove = null;

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
        if (instaUrl != null && instaUrl.contains("https://www.instagram.com/p/") && !instaUrl.equals(mCurrentUrlRemove)) {
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

    @OnClick({R.id.text_user_name, R.id.icon_profile})
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
        // Save URL to remove
        mCurrentUrlRemove = pastClipboard();
        // Init View
        initView(null);
    }

    @OnClick(R.id.image_action_download)
    protected void onClickDownload() {
        if (PermissionsUtils.checkAndRequest(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionsUtils.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                "You need to enable this permission to download the picture", null)) {
            savePicture();
        }
    }

    private void savePicture() {
        Bitmap bitmap = ((BitmapDrawable)mImageToDownload.getDrawable()).getBitmap();
        if (downloadPicture(bitmap, mIdImage + ".jpg")) {
            Snackbar.make(mCoordinatorLayout, "Photo saved !", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mCoordinatorLayout, "Save photo failed", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean downloadPicture(Bitmap bitmap, String fileName) {
        boolean result = false;
        OutputStream output = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, fileName);
            output = new FileOutputStream(sdImageMainDirectory);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @OnClick(R.id.btn_open_insta)
    protected void onClickOpenInstagram() {
        // Close Keyboard
        closeKeyboard();
        // Start Instagram App or Open Market
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

    @OnClick(R.id.btn_load_it)
    protected void onClickLoadIt() {
        String editVal = editInstaUrl.getText().toString();
        if (editVal != null) {
            if (editVal.contains("https://www.instagram.com/p/")) {
                // Clear current Url Remove
                mCurrentUrlRemove = null;
                // Clear editText
                editInstaUrl.setText(null);
                // Close Keyboard
                closeKeyboard();
                // Init View
                initView(editVal);
            } else {
                // Is Not Insta URL
                Snackbar.make(mCoordinatorLayout, "Is not an Instagram photo URL", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            // Empty
            Snackbar.make(mCoordinatorLayout, "Input Empty", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

                    String json = html.substring(html.indexOf(startJson) + startJson.length(),
                            html.indexOf(endJson));

                    InstaData instaData = new Gson().fromJson(json, InstaData.class);
                    InstaMedia instaMedia = instaData.getEntryData().getPostPage().get(0).getMedia();
                    InstaOwner instaOwner = instaMedia.getOwner();
                    String userName = instaOwner.getUsername();
                    String fullName = instaOwner.getFullname();
                    String urlProfile = instaOwner.getProfilePicUrl();

                    EventBus.getDefault().post(new EventInstaPictureLoad(userName, fullName, instaUrl + "media/?size=l", urlProfile, instaMedia.getId()));

                } catch (Exception ex) {
                    // Send Fail
                    EventBus.getDefault().post(new EventInstaPictureLoad(false));
                }

            }
        }).start();
    }

    public void onEventMainThread(final EventInstaPictureLoad eventInstaPictureLoad) {
        if (eventInstaPictureLoad != null && eventInstaPictureLoad.isLoadWell()) {
            // Save userName and id image
            mUserName = eventInstaPictureLoad.getUserName();
            mIdImage = eventInstaPictureLoad.getIdImage();

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
        } else {
            initView(null);
            // Show erreur
            Snackbar.make(mCoordinatorLayout, "Load photo failed", Snackbar.LENGTH_SHORT).show();
        }
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
                // Show Dialog Information
                new AlertDialog.Builder(this)
                        .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getResources().getString(R.string.info_message))
                        .setCancelable(false)
                        .setNeutralButton("RATE THIS APP", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                } catch (ActivityNotFoundException e) {
                                    Snackbar.make(mCoordinatorLayout, "You don't have Market App", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton(getResources().getString(android.R.string.ok), null)
                        .create().show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsUtils.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    savePicture();
                }
                break;
        }
    }

}
