package com.dat255tesla.busexplorer.database;

import junit.framework.TestCase;

public class InfoNodeTest extends TestCase {

    private InfoNode a;
    private InfoNode b;

    private final int ID = 1;
    private final String TITLE = "A";
    private final double LAT = 57.6883697;
    private final double LONG = 11.9764802;
    private final int TYPE = 0;
    private final String INFO = "Info_1";
    private final String ADDR = "Addr_1";
    private final long DATE = 111111111;
    private final String OBJID = "ObjID_1";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        a = new InfoNode(ID, TITLE, LAT, LONG, TYPE, INFO, ADDR, DATE, OBJID);
        b = new InfoNode(2, "B", 57.6883696, 11.9764801, 1, "Info_2", "Addr_2", 222222222, "ObjID_2");
    }

    /**
     * Test getters (methods) after creating InfoNode using constructor (no setters used in unit).
     */
    public void testUnit() {
        assertTrue(a.getId() == ID);
        assertTrue(a.getTitle().equals(TITLE));
        assertTrue(a.toString().equals(a.getTitle()));
        assertTrue(a.toString().equals(TITLE));
        assertTrue(a.getLatitude() == LAT);
        assertTrue(a.getLongitude() == LONG);
        assertTrue(a.getType() == TYPE);
        assertTrue(a.getInfo().equals(INFO));
        assertTrue(a.getAddress().equals(ADDR));
        assertTrue(a.getLatestModified() == DATE);
        assertTrue(a.getObjId().equals(OBJID));
        assertTrue(!a.equals(b));
    }

}
