<img src="/preview/preview.gif" alt="sample" title="sample" width="300" height="553" align="right" />

SaveInsta
=========
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![Twitter](https://img.shields.io/badge/Twitter-@LopezMikhael-blue.svg?style=flat)](http://twitter.com/lopezmikhael)

With **SaveInsta** download all Instagram photos and videos that you love.

> This application is an example of the implementation of the **dynamic update of your theme** based on a main color. The application retrieve the **dominant color of an image** and change the theme of the activity in runtime.

Guidelines Example
-----

**3 Steps to download photo/video from Instagram:**
  1. Open your Instagram, choose Copy Share URL on photo/video you want to save
  2. Open SaveInsta and let it do the magic
  3. Back to SaveInsta, download Photo/Video you love by click to Save button at the bottom right of SaveInsta, or click to Close button at the top right to remove. Open Gallery to see the result!

Get Dominant Color of ImageView
-----

You need to Download [**DominantImageColor**](app/src/main/java/com/mikhaellopez/saveinsta/utils/DominantImageColor.java) and write this method in your project:

```java
public static int getDominantColor(ImageView imageView) {
  int color = 0;
  try {
    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    String dominantColor = DominantImageColor.getDominantColorOfImage(bitmap);
    color = Color.parseColor(dominantColor);
  } catch (Exception ex) {
    ex.printStackTrace();
  }
  return color;
}
```

Set Theme Programmatically
-----

**3 components** must be modified for best possible immersion:

- `mToolbar.setBackgroundColor(dominantColor);`
- `getWindow().setStatusBarColor(dominantColor);`
- `getWindow().setNavigationBarColor(dominantColor);`

For a better visual you need to add animation for each change:

> For that, you also get the current color of the theme `colorPrimary`. To do that just call `ContextCompat.getColor(this, R.color.colorPrimary);`. 

```java
ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, dominantColor);
colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
      mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
    }
});
colorAnimation.start();
```

:warning: Please note that the status bar is not the same color as your toolbar. So you have to darken to meet the best practices of Android Material Design. For that use this method:

```java
public static int darkerColor(int color) {
  float[] hsv = new float[3];
  Color.colorToHSV(color, hsv);
  hsv[2] *= 0.8f; // value component
  return Color.HSVToColor(hsv);
}
```

:warning: Attention to the color of the text and icons on your toolbar. You must detect if the dominant color is a dark color or not. If this is the case the color of your text should be light and vice versa.

```java
public static boolean isColorDark(int color) {
  double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
  if (darkness < 0.5) {
    return false; // It's a light color
  } else {
    return true; // It's a dark color
  }
}
```

Update in Runtime 
-----

This treatment can be long, so it is best not to leave it in the UI thread.
The following example retrieves the dominant color in a separate thread, and then returns to the UI thread to change the theme:

> `mCurrentThemeColor` is the main color theme of your application. To recover just do `ContextCompat.getColor(this, R.color.colorPrimary);`.

```java
final Handler handler = new Handler();
new Thread(new Runnable() {
  @Override
  public void run() {
    // Get Dominant Color
    final int color = getDominantColor(mImageToDownload);
    if (color != 0) {
        handler.post(new Runnable() {
            @Override
            public void run() {
              // Set Theme Color
              setThemeColor(mCurrentThemeColor, color);
            }
        });
    } else if (BuildConfig.DEBUG) {
        Log.e(getClass().getName(), "Color Unknown");
    }
  }
}).start();
```

:point_right: [**You can see a full implementation here**](app/src/main/java/com/mikhaellopez/saveinsta/activity/MainActivity.java)

License
-----

**SaveInsta** by [Lopez Mikhael](http://mikhaellopez.com/) is licensed under a [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
