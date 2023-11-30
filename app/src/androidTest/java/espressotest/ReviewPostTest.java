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
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.R;
import com.example.cpen321mappost.User;
import org.junit.After;
import org.junit.Before;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReviewPostTest {
    private UiDevice uiDevice;

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
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
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

        int xCoordinate = 540; // Determine the X coordinate
        int yCoordinate = 1080; // Determine the Y coordinate

        // Perform the click
        uiDevice.click(xCoordinate, yCoordinate);
        try {
            Thread.sleep(10000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.createPostButton)).check(matches(isDisplayed()));
        onView(withId(R.id.reviewPostsButton)).check(matches(isDisplayed()));

        onView(withId(R.id.reviewPostsButton)).perform(click());
        try {
            Thread.sleep(5000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.postsRecyclerView)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "Click on the first item of the RecyclerView.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.getChildAt(0).performClick();
            }
        });
        try {
            Thread.sleep(5000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.textViewTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewMainContent)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextComment)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonComment)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLike)).check(matches(isDisplayed()));

    }


}
