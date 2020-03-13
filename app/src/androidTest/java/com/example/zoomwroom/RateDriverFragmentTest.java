package com.example.zoomwroom;

import androidx.fragment.app.FragmentActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class RateDriverFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<FragmentTestActivity> rule = new androidx.test.rule.ActivityTestRule<>(
            FragmentTestActivity.class, true, true);


    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testDriverNameDisplay() {
        int n_tests = 3;
        String driverName = "Test Driver";
        String driverName_i;

        for (int i = 0; i < n_tests; i++) {
            driverName_i = driverName + i;
            solo.assertCurrentActivity("Wrong Activity", FragmentTestActivity.class);
            ((FragmentTestActivity) solo.getCurrentActivity()).setDriverName(driverName_i);
            solo.clickOnButton("TEST1");    // click "TEST1" button
            assertTrue(solo.waitForText(driverName_i, 1, 1000));
            solo.clickOnButton("Confirm");
        }
    }

    @Test
    public void testPositiveRate() {
        solo.assertCurrentActivity("Wrong Activity", FragmentTestActivity.class);
        ((FragmentTestActivity) solo.getCurrentActivity()).setDriverName("test_driver");
        solo.clickOnButton("TEST1");    // click "TEST1" button
        solo.clickOnImage(0);      // click positive rate button
        solo.clickOnButton("Confirm");
        assertTrue(solo.waitForText("THUMBS UP", 1, 1000));
    }

    @Test
    public void testNegativeRate() {
        solo.assertCurrentActivity("Wrong Activity", FragmentTestActivity.class);
        ((FragmentTestActivity) solo.getCurrentActivity()).setDriverName("test_driver");
        solo.clickOnButton("TEST1");    // click "TEST1" button
        solo.clickOnImage(1);      // click positive rate button
        solo.clickOnButton("Confirm");
        assertTrue(solo.waitForText("THUMBS DOWN", 1, 1000));
    }

    @Test
    public void testNoRate() {
        solo.assertCurrentActivity("Wrong Activity", FragmentTestActivity.class);
        ((FragmentTestActivity) solo.getCurrentActivity()).setDriverName("test_driver");
        solo.clickOnButton("TEST1");    // click "TEST1" button
        solo.sleep(500);
        solo.clickOnButton("Confirm");
        assertTrue(solo.waitForText("NO RATE", 1, 1000));
    }
}
