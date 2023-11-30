package espressotest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.example.cpen321mappost.AuthenticationActivity;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.PostActivity;
import com.example.cpen321mappost.PostDetailActivity;
import com.example.cpen321mappost.PostPreviewListActivity;
import com.example.cpen321mappost.R;
import com.example.cpen321mappost.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReviewPostTest {
    private UiDevice uiDevice;


    private CountingIdlingResource idlingResource = new CountingIdlingResource("DATA_LOADER");

    @Rule
    public ActivityScenarioRule<MapsActivity> activityScenarioRule = new ActivityScenarioRule<>(MapsActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION // If needed
    );

    @Before
    public void setUp() throws Exception {
        MapsActivity.TEST_MODE=true;
        User.TEST_MODE=true;
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
    @Test
    public void testReviewPost() throws Exception {
        MapsActivity.TEST_MODE = true;
        User.TEST_MODE = true;

        try {
            Thread.sleep(15000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Step 1: Click on an empty area
//        onView(withId(R.id.map)).perform(click());
        int xCoordinate = 540; // Determine the X coordinate
        int yCoordinate = 1080; // Determine the Y coordinate

        // Perform the click
        uiDevice.click(xCoordinate, yCoordinate);
        try {
            Thread.sleep(10000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Step 2: Check for bottom sheet dialog and its contents
        onView(withId(R.id.createPostButton)).check(matches(isDisplayed()));
        onView(withId(R.id.reviewPostsButton)).check(matches(isDisplayed()));

        onView(withId(R.id.reviewPostsButton)).perform(click());
        intended(hasComponent(PostPreviewListActivity.class.getName()));
        idlingResource.increment();
        idlingResource.decrement();

////
//        onView(withId(R.id.postsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        try {
//            Thread.sleep(3000); // Wait for 3 seconds
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        intended(hasComponent(PostDetailActivity.class.getName()));
//        onView(withId(R.id.textViewTitle)).check(matches(isDisplayed()));
//        onView(withId(R.id.imageViewPost)).check(matches(isDisplayed()));
//        onView(withId(R.id.textViewMainContent)).check(matches(isDisplayed()));
//        onView(withId(R.id.editTextComment)).check(matches(isDisplayed()));
//        onView(withId(R.id.buttonComment)).check(matches(isDisplayed()));
//        onView(withId(R.id.buttonLike)).check(matches(isDisplayed()));


    }


}
