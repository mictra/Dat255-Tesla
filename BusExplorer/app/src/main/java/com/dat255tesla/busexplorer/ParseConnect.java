package com.dat255tesla.busexplorer;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseConnect extends Application {

    @Override
    public void onCreate() {
        // This class is instantiated first before any of the component's of this application
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Connect to Parse datastore with username and key.
        Parse.initialize(this, "5XyBDVThoHneuBlqK7jHLd5Rhc555P3KL1MZ0vZP", "YXqzrViw9JXqWoetV6GxKeugbyERTeysUVAUnSmx");
        /*
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        */
    }
}
