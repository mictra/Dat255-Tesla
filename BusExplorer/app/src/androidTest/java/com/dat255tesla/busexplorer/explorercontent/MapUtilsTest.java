package com.dat255tesla.busexplorer.explorercontent;

import com.dat255tesla.busexplorer.database.InfoNode;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy lena on 2015-10-28.
 */
public class MapUtilsTest extends TestCase {
    private final List<InfoNode> exampleList = new ArrayList<>();

    private final InfoNode node1 =
            new InfoNode(1, "4G Bar", 57.7093239, 11.9664634, 1, " ", " ", 111111111, "ObjID");
    private final InfoNode node2 =
            new InfoNode(2, "Göteborgsoperan", 57.710612, 11.96307, 2, " ", " ", 222222222, "ObjID");
    private final InfoNode node3 =
            new InfoNode(3, "SVT Göteborg", 57.7121861, 11.9462395, 3, " ", " ", 333333333, "ObjID");

    private final LatLng loc1 = new LatLng(55.676097, 12.568337); // Copenhagen
    private final LatLng loc2 = new LatLng(40.712784, -74.005941); // New York

    @Override
    protected void setUp() throws Exception {
        // Set up tests

        exampleList.add(node1);
        exampleList.add(node2);
        exampleList.add(node3);
    }

    public void testListFilters() {
        // Test filterValues
        /**
         * Checks if a list given certain types is filtered correctly
         * In this case, 2 of the 3 nodes will remain, the third should be filtered out.
         */
        boolean[] types = {true,true,false}; // Types 1 and 2.
        List<InfoNode> filteredList = MapUtils.filterValues(exampleList, types);
        assertTrue(filteredList.size() == 2);
        assertTrue(filteredList.contains(node1));
        assertTrue(filteredList.contains(node2));
        assertFalse(filteredList.contains(node3));

        types[2] = true; // Types 1, 2 and 3.
        filteredList = MapUtils.filterValues(exampleList, types);
        assertTrue(filteredList.size() == 3);
        assertTrue(filteredList.contains(node1));
        assertTrue(filteredList.contains(node2));
        assertTrue(filteredList.contains(node3));

        types[0] = false; types[1] = false; types[2] = false;
        filteredList = MapUtils.filterValues(exampleList, types);
        assertTrue(filteredList.isEmpty());
        assertFalse(filteredList.contains(node1));
        assertFalse(filteredList.contains(node2));
        assertFalse(filteredList.contains(node3));
    }

    public void testSortedList() {
        // Test sortByDistance
        /**
         * Test sorting the list by distance from a node.
         * This node is normally a bus stop.
         * The current layout is: node1 - node2 - node3.
         * I.e node1 is furthest away from node3, and node2 is in the middle
         */
        List<InfoNode> sortedList = MapUtils.sortByDistance(exampleList, node3.getTitle());
        assertTrue(sortedList.size() == 2);
        assertTrue(sortedList.get(0) == node2);
        assertTrue(sortedList.get(1) == node1);
        assertTrue(sortedList.contains(node1));
        assertTrue(sortedList.contains(node2));
        assertFalse(sortedList.contains(node3));

        sortedList = MapUtils.sortByDistance(exampleList, node1.getTitle());
        assertTrue(sortedList.size() == 2);
        assertTrue(sortedList.get(0) == node2);
        assertTrue(sortedList.get(1) == node3);
        assertFalse(sortedList.contains(node1));
        assertTrue(sortedList.contains(node2));
        assertTrue(sortedList.contains(node3));

        sortedList = MapUtils.sortByDistance(exampleList, " ");
        assertTrue(sortedList.equals(exampleList));
    }

    public void testRmcParse() {
        // Tests ParseRMC
        /**
         * This example should return the following LatLng:
         * 57 + (42.5446 / 60) = 57.70908 +- .00001
         * 11 + (58.0166 / 60) = 11.96694 +- .00001
         */
        double latLow = 57.70907;
        double latHigh = 57.70909;

        double lngLow = 11.96693;
        double lngHigh = 11.96695;

        String ex = "$GPRMC,131420,A,5742.5446,N,01158.0166,E,,,2992015,,,A*4A";
        LatLng latLng = MapUtils.ParseRMC(ex);

        assertTrue(latLng.latitude > latLow && latLng.latitude < latHigh);
        assertTrue(latLng.longitude > lngLow && latLng.longitude < lngHigh);
    }

    public void testXmlParse() {
        String system_id = "10062621";
        String ex = "<?xml version=\"1.0\" encoding \"UTF-8\"?> <system version=\"-1.0\"> " +
                "<system_id type=\"integer\">" + system_id + "</system_id> </system>";

        try {
            String xml_id = MapUtils.parseSystemIDXML(ex);
            assertTrue(xml_id.equals(system_id));
            assertFalse(xml_id.equals(""));
            assertFalse(xml_id.equals(system_id + "1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
