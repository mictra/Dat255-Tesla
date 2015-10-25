package com.dat255tesla.busexplorer.server;


import android.app.Application;

import com.parse.Parse;

public class ParseConnect extends Application {

    /**
     * This is done when application starts.
     * Connect to Parse server with username and key.
     * When authenticated, we can use Parse API to retrieve datastore/database from server.
     */
    @Override
    public void onCreate() {
        // This class is instantiated first before any of the component's of this application
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Connect to Parse datastore with username and key.
        Parse.initialize(this, "5XyBDVThoHneuBlqK7jHLd5Rhc555P3KL1MZ0vZP", "YXqzrViw9JXqWoetV6GxKeugbyERTeysUVAUnSmx");
    }

}
