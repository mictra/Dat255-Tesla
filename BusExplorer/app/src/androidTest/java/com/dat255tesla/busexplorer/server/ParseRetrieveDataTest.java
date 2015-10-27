package com.dat255tesla.busexplorer.server;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.dat255tesla.busexplorer.database.IValuesChangedListener;
import com.dat255tesla.busexplorer.database.InfoDataSource;
import com.dat255tesla.busexplorer.database.InfoNode;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * OBS: This test class is dependent on the server database, and might not work properly
 * if the test-data made for this class on the server side has changed.
 * The test-data hard coded on the server is meant to remain unchanged.
 */
public class ParseRetrieveDataTest extends AndroidTestCase implements IValuesChangedListener {
    private List<ParseObject> objects;
    private List<ParseObject> markers;
    private InfoDataSource ids;

    /**
     * Data to be matched with server data (hard coded on server side).
     */
    private final String SERVER_FOO_1 = "A";
    private final String SERVER_FOO_2 = "B";
    private final String SERVER_FOO_3 = "C";
    private final int SERVER_BAR_1 = 1;
    private final int SERVER_BAR_2 = 2;
    private final int SERVER_BAR_3 = 3;

    private CountDownLatch signal;

    /**
     * Finds and retrieves data from server database (hard coded on server side).
     * Saves it as a list in #objects.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        objects = ParseQuery.getQuery("TestObject").find();
        markers = ParseQuery.getQuery("Marker").find();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test");
        ids = new InfoDataSource(context);
        ids.setValuesChangedListener(this);
        signal = new CountDownLatch(1);
    }

    /**
     * Tears down, clears the database tables and closes the writable database.
     *
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ids.close();
    }

    /**
     * Checks the exact amount and fields of retrieved data.
     *
     * @throws ParseException
     */
    public void testRetrieveData() throws ParseException {
        assertTrue(objects.size() == 3);
        for (ParseObject o : objects) {
            switch (o.getString("foo")) {
                case SERVER_FOO_1:
                    assertTrue(o.getInt("bar") == SERVER_BAR_1);
                    break;
                case SERVER_FOO_2:
                    assertTrue(o.getInt("bar") == SERVER_BAR_2);
                    break;
                case SERVER_FOO_3:
                    assertTrue(o.getInt("bar") == SERVER_BAR_3);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }
    }

    /**
     * Checks that the internal database is updated with server database if needed (it will in this case),
     * by comparing latestModified date and comparing list (values) before and after update.
     */
    public void testUpdateInternalDB() throws SQLException, InterruptedException {
        ids.open();
        /**
         * Hard coded data inserted to internal database before checking if update is needed.
         * The date (in long format) that is being hard coded has been made sure to be
         * before (smaller than) a date in the server database when comparing to ensure update.
         */
        ids.createInfoNode("A", 0, 0, 0, "Info_1", "Addr_1", 1442527200, "ObjID");
        ids.createInfoNode("B", 0, 0, 0, "Info_2", "Addr_2", 1442613600, "ObjID");
        ids.createInfoNode("C", 0, 0, 0, "Info_3", "Addr_3", 1442700000, "ObjID");
        List<InfoNode> oldNodes = ids.getAllInfoNodes();
        long oldLatestMod = ids.getLatestModified();
        // This method is done in the background, we use #signal to await a callback response.
        ids.updateDatabaseIfNeeded();
        signal.await();
        long newLatestMod = ids.getLatestModified();
        // Checks that the internal database has been updated
        assertTrue(oldLatestMod < newLatestMod);
        List<InfoNode> newNodes = ids.getAllInfoNodes();
        // Checks that the contents before and after the update has been updated/changed
        assertTrue(!oldNodes.equals(newNodes));
    }

    /**
     * Listener method from implementing interface.
     * Needed to get notified when updated with new values from server database.
     *
     * @param values
     */
    @Override
    public void originalValuesChanged(List<InfoNode> values) {
        signal.countDown();
    }

}
