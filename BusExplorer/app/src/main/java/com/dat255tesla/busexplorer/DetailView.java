package com.dat255tesla.busexplorer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailView extends AppCompatActivity {

    private InfoNode node;
    private TextView headline;
    private TextView subheadline;
    private WebView description;
    private LinearLayout imageGallery;

    private List<Bitmap> imgs;
    private List<Bitmap> thumbs;

    private HashMap<String, Bitmap> imgMap;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system animation time duration, in milliseconds.
     */
    private int mAnimationDuration;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail_view);

        imgs = new ArrayList<>();
        thumbs = new ArrayList<>();
        imgMap = new HashMap<>();

        node = (InfoNode) getIntent().getSerializableExtra("InfoNode");

        headline = (TextView) findViewById(R.id.dv_headline);
        headline.setText(node.getTitle());
        subheadline = (TextView) findViewById(R.id.dv_subheadline);
        subheadline.setText(node.getAddress());
        imageGallery = (LinearLayout) findViewById(R.id.dv_imageGallery);
        description = (WebView) findViewById(R.id.dv_description);

        getImagesFromServer();

        description.loadUrl("file:///android_asset/detailview/" + node.getInfo()); //TODO: Store html file on server and retrieve it from there?

        // Retrieve and cache the system's default medium animation time.
        mAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }

    /*
    Retrieves and displays images from server database.
    This method has inner callback functions/methods and is done in the background.
     */
    private void getImagesFromServer() {
        // Background method, displays images from server when its done retrieving them.
        ParseQuery.getQuery("Marker").getInBackground(node.getObjId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) { // If there are no exceptions, continue

                    parseObject.getRelation("imgs").getQuery().findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            try {
                                for (ParseObject object : list) {
                                    ParseFile img = object.getParseFile("image");
                                    byte[] data = img.getData();
                                    Bitmap bmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imgMap.put(img.getName().substring(42), bmap); // Hard coded, lel
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            addImagesToGallery();
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void setFields() {
//        headline.setText(title);
//        subheadline.setText(address);
//        if (noimages > 0) {
//            //createArrays();
//            addImagesToGallery();
//        }
//        description.loadUrl("file:///android_asset/detailview/" + info);
//    }

//    private void createArrays() {
//        images = new Integer[noimages];
//        thumbs = new Integer[noimages];
//
//        for (int i = 0; i < noimages; i++) {
//            images[i] = getResources().getIdentifier(imagename + (i+1), "drawable", getPackageName());
//            thumbs[i] = getResources().getIdentifier(imagename + (i+1) + "_thumb", "drawable", getPackageName());
//        }
//    }

    /**
     * Building the image gallery
     */
    private void addImagesToGallery() {
        imageGallery.removeAllViews();
        for (String key : imgMap.keySet()) {
            if (key.contains("thumb")) {
                imageGallery.addView(getImageView(imgMap.get(key), imgMap.get(key.replace("_thumb", ""))));
            }
        }
    }

    private View getImageView(final Bitmap thumb, final Bitmap img) {
        final com.dat255tesla.busexplorer.TouchHighlightImageButton imageButton = new com.dat255tesla.busexplorer.TouchHighlightImageButton(getApplicationContext());
        int width = Math.round(200 * (getResources().getDisplayMetrics().densityDpi / 160));
        int height = Math.round(200 * (getResources().getDisplayMetrics().densityDpi / 160));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 10, 0);
        imageButton.setLayoutParams(lp);
        imageButton.setImageBitmap(thumb);
        imageButton.setScaleType(TouchHighlightImageButton.ScaleType.CENTER_CROP);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(imageButton, img);
                //Toast.makeText(getApplicationContext(), ""+test,Toast.LENGTH_SHORT).show();
            }
        });
        return imageButton;
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap imageResId) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.dv_expanded_image);
        expandedImageView.setImageBitmap(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
