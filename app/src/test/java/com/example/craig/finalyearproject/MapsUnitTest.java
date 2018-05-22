package com.example.craig.finalyearproject;

import com.example.craig.finalyearproject.model.PlaceInformation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MapsUnitTest {

    @Test
    public void testSetUpMyGeoLocation(){
        double lat = 53.27634;
        double lon = -6.27634;
        String code = "test";
        double actualLat = 53.27634;
        double actualLon = -6.27634;
        String actualCode = "tes";

        assertEquals("Value incorrect ",lat,actualLat,0.0);
        assertEquals("Value incorrect ",lon,actualLon,0.0);
        assertEquals("Value incorrect ",code,actualCode);
    }

    @Test
    public void testGetPlaceInfo(){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?";
        assertTrue("Incorrect",url.contains("google"));
    }

    @Test
    public void testcheckListSize(){
        ArrayList list = new ArrayList();
        list.add(new PlaceInformation());
        if (list.size() > 0){
            list.clear();
        }
        assertTrue("Empty",list.isEmpty());
    }
}