package com.dat255tesla.busexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Button helloButton;
    private TextView helloText;
    private EditText editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloButton = (Button) findViewById(R.id.helloButton);
        helloText = (TextView) findViewById(R.id.helloText);
        editName = (EditText) findViewById(R.id.editName);

        View.OnClickListener hello = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                helloText.setText("Hello, " + name + "!");
            }
        };

        helloButton.setOnClickListener(hello);
    }

}
