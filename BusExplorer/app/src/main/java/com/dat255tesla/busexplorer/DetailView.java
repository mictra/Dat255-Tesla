package com.dat255tesla.busexplorer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
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

public class DetailView extends AppCompatActivity {

    private Integer images[] = {R.drawable.poseidon1, R.drawable.poseidon2, R.drawable.poseidon3};
    private Integer thumbs[] = {R.drawable.poseidon1_thumb, R.drawable.poseidon2_thumb, R.drawable.poseidon3_thumb};
    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail_view);

        TextView headline = (TextView) findViewById(R.id.dv_headline);
        headline.setText("Poseidon");

        TextView subheadline = (TextView) findViewById(R.id.dv_subheadline);
        subheadline.setText("(Götaplatsen 5b)");

        addImagesToGallery();

//        TextView desc = (TextView) findViewById(R.id.dv_descriptiontest);
//        desc.setText("Poseidon med brunnskar (eller Poseidonbrunnen) är en bronsskulptur som står på Götaplatsen i Göteborg. Statyn, som föreställer havsguden Poseidon, är skapad av Carl Milles och invigdes 1931. Den är ett av Göteborgs mest kända landmärken.");
//        desc.append("\n\nDen sju meter höga skulpturen håller en fisk i höger hand och en snäcka i vänster hand.");
//        desc.append("\n\nI brunnskaret återfinns sex mindre skulpturer och reliefer med olika vidunder, tritoner, najader, fiskar och sjöjungfrur. Brunskaret är 120 cm högt och själva poseidonskulpturen ytterligare 7 meter hög.[4] Statyn var från början tänkt att heta Neptunus, det romerska namnet på havets gud.[5] Brunskaret tillverkades vid Herman Bergmans bronsgjuteri i Stockholm men mittgrupperna med Poseidonskulpturen göts vid Lauriz Rasmussens Broncestøberi i Köpenhamn.");

        WebView description = (WebView) findViewById(R.id.dv_description);

        description.loadData(getHtmlString(), "text/html; charset=utf-8", "UTF-8");

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

    }

    private String getHtmlString() {
        String html;
        html =  "<html>"
                + "<head>"
                + "<style type=\"text/css\">"
                + "body" + getBodyStyle()
                + "p" + getPStyle()
                + "div.happyhour" + getHappyHourStyle()
                + "</style>"
                + "</head>"
                + "<body>"
                + getHappyHour(Math.random() > 0.5)
                + "<p>"
                + getDescription()
                + "</p>";
        return html;
    }

    private String getHappyHourStyle() {
        return "{background-color:#ff5722; " +
                "color:white; " +
                "padding: 5 5 5 5; " +
                "font-weight:bold; " +
                "font-size:20pt; " +
                "border-radius: 23px; " +
                "text-align:center;}";
    }

    private String getPStyle() {
        return "{text-align:left;" +
                "font-size:12pt;}";
    }

    private String getBodyStyle() {
        return "{background-color: #eeeeee; " +
                "margin: 0 0 0 0; " +
                "padding: 0 0 0 0;}";
    }

    private String getDescription() {
        String desc = "Poseidon med brunnskar (eller Poseidonbrunnen) är en bronsskulptur som står på Götaplatsen i Göteborg. Statyn, som föreställer havsguden Poseidon, är skapad av Carl Milles och invigdes 1931. Den är ett av Göteborgs mest kända landmärken.<br /><br />Den sju meter höga skulpturen håller en fisk i höger hand och en snäcka i vänster hand.<br /><br />I brunnskaret återfinns sex mindre skulpturer och reliefer med olika vidunder, tritoner, najader, fiskar och sjöjungfrur. Brunskaret är 120 cm högt och själva poseidonskulpturen ytterligare 7 meter hög.[4] Statyn var från början tänkt att heta Neptunus, det romerska namnet på havets gud.[5] Brunskaret tillverkades vid Herman Bergmans bronsgjuteri i Stockholm men mittgrupperna med Poseidonskulpturen göts vid Lauriz Rasmussens Broncestøberi i Köpenhamn.</body></html>";
        return desc;
    }

    private String getHappyHour(boolean b) {
        if (b) {
            String hh = "<div class=\"happyhour\">"
                    + "HAPPYHOUR TODAY !!!"
                    + "</div>";
            return hh;
        } else {
            return "";
        }
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

    /**
     * Building the image gallery
     */
    private void addImagesToGallery() {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.dv_imageGallery);
//        for (Integer image : images) {
//            imageGallery.addView(getImageView(image));
//        }
        for (int i = 0; i < images.length; i++) {
            imageGallery.addView(getImageView(images[i], thumbs[i]));
        }
    }

    private View getImageView(final Integer image, Integer thumb) {
        //final ImageView imageView = new ImageView(getApplicationContext());
        final com.dat255tesla.busexplorer.TouchHighlightImageButton imageButton = new com.dat255tesla.busexplorer.TouchHighlightImageButton(getApplicationContext());
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(500, 650);
        int width = (int) Math.round(175 * (getResources().getDisplayMetrics().densityDpi/160));
        int height = (int) Math.round(175 * (getResources().getDisplayMetrics().densityDpi/160));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 10, 0);
        imageButton.setLayoutParams(lp);
        imageButton.setImageResource(thumb);
        imageButton.setScaleType(TouchHighlightImageButton.ScaleType.CENTER_CROP);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo: some code here
                zoomImageFromThumb(imageButton, image);
//                String filename = (String) getResources().getText(image);
//                Toast.makeText( getApplicationContext(),
//                        filename.substring(filename.lastIndexOf("/")+1) + " clicked",
//                                Toast.LENGTH_SHORT).show();
            }
        });
        return imageButton;
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.dv_expanded_image);
        expandedImageView.setImageResource(imageResId);

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
        set.setDuration(mShortAnimationDuration);
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
                set.setDuration(mShortAnimationDuration);
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
