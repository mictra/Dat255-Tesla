package com.dat255tesla.busexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailView extends AppCompatActivity {

    private Integer images[] = {R.drawable.poseidon1, R.drawable.poseidon2, R.drawable.poseidon3};

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

//        TextView desc = (TextView) findViewById(R.id.dv_description);
//        desc.setText("Poseidon med brunnskar (eller Poseidonbrunnen) är en bronsskulptur som står på Götaplatsen i Göteborg. Statyn, som föreställer havsguden Poseidon, är skapad av Carl Milles och invigdes 1931. Den är ett av Göteborgs mest kända landmärken.");
//        desc.append("\n\nDen sju meter höga skulpturen håller en fisk i höger hand och en snäcka i vänster hand.");
//        desc.append("\n\nI brunnskaret återfinns sex mindre skulpturer och reliefer med olika vidunder, tritoner, najader, fiskar och sjöjungfrur. Brunskaret är 120 cm högt och själva poseidonskulpturen ytterligare 7 meter hög.[4] Statyn var från början tänkt att heta Neptunus, det romerska namnet på havets gud.[5] Brunskaret tillverkades vid Herman Bergmans bronsgjuteri i Stockholm men mittgrupperna med Poseidonskulpturen göts vid Lauriz Rasmussens Broncestøberi i Köpenhamn.");

        WebView desc = (WebView) findViewById(R.id.dv_description);
        String customHtml = "<html>"
                + "<head>"
                + "<style type=\"text/css\">"
                + "body{color:green; text-align:left; font-size:11pt;}"
                + "</style>"
                + "</head>"
                + "<body>Poseidon med brunnskar (eller Poseidonbrunnen) är en bronsskulptur som står på Götaplatsen i Göteborg. Statyn, som föreställer havsguden Poseidon, är skapad av Carl Milles och invigdes 1931. Den är ett av Göteborgs mest kända landmärken.<br /><br />Den sju meter höga skulpturen håller en fisk i höger hand och en snäcka i vänster hand.<br /><br />I brunnskaret återfinns sex mindre skulpturer och reliefer med olika vidunder, tritoner, najader, fiskar och sjöjungfrur. Brunskaret är 120 cm högt och själva poseidonskulpturen ytterligare 7 meter hög.[4] Statyn var från början tänkt att heta Neptunus, det romerska namnet på havets gud.[5] Brunskaret tillverkades vid Herman Bergmans bronsgjuteri i Stockholm men mittgrupperna med Poseidonskulpturen göts vid Lauriz Rasmussens Broncestøberi i Köpenhamn.</body></html>";
        desc.loadData(customHtml, "text/html; charset=utf-8", "UTF-8");

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
        for (Integer image : images) {
            imageGallery.addView(getImageView(image));
        }
    }

    private View getImageView(final Integer image) {
        final ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setImageResource(image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo: some code here
                String filename = (String) getResources().getText(image);
                Toast.makeText( getApplicationContext(),
                        filename.substring(filename.lastIndexOf("/")+1) + " clicked",
                                Toast.LENGTH_SHORT).show();
            }
        });
        return imageView;
    }
}
