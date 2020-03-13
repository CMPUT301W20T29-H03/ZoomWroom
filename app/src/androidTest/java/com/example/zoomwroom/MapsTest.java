package com.example.zoomwroom;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class MapsTest {


    private MapsActivity mockMapActivity() {
        return new MapsActivity();
    }
    @Test
    void testZeroDistance() {
        MapsActivity map = mockMapActivity();
        assertEquals(0, map.getDistance(0, 0, 0, 0));
    }
    @Test
    void testDistance() {
        MapsActivity map = mockMapActivity();
        assertEquals(1095 ,
                MapsActivity.round(
                        map.getDistance(10, 10, 10, 20), 0));
    }
}
