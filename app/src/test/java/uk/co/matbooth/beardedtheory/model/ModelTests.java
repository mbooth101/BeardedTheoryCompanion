package uk.co.matbooth.beardedtheory.model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ModelTests {
    @Test
    public void dayRepresentation() throws Exception {
        Day day = new Day();
        day.setDate(new Date(1464350400000L));
        assertEquals("Fri 27th", day.getName());
        day.setDate(new Date(1458691200000L));
        assertEquals("Wed 23rd", day.getName());
        day.setDate(new Date(1457827200000L));
        assertEquals("Sun 13th", day.getName());
    }
}