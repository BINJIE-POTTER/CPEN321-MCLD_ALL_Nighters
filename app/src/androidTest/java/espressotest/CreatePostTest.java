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

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.cpen321mappost.MapsActivity; // Import from your main app package

import com.example.cpen321mappost.AuthenticationActivity;
import com.example.cpen321mappost.MainActivity;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.PostActivity;
import com.example.cpen321mappost.R;
import com.example.cpen321mappost.User;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreatePostTest {
    private UiDevice uiDevice;


    @Rule
    public ActivityScenarioRule<MapsActivity> activityScenarioRule = new ActivityScenarioRule<>(MapsActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    @Before
    public void setUp() throws Exception {
        User.TEST_MODE = true;
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        Intents.init();

    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }


    @Test
    public void testCreatePostFlow() {
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

        // Step 3: Click on “CREATE A POST” menu item
        onView(withId(R.id.createPostButton)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.wait(Until.hasObject(By.text("Allow")), 3000);
        UiObject2 allowButton = uiDevice.findObject(By.text("Allow"));
        if (allowButton != null) {
            allowButton.click();
        }
        intended(hasComponent(PostActivity.class.getName()));
        PostActivity.TEST_MODE = true;

        // Step 4: Check UI elements in PostActivity
        onView(withId(R.id.imgPreview)).check(matches(isDisplayed()));
        onView(withId(R.id.titleEditText)).check(matches(withHint("Title")));
        onView(withId(R.id.mainTextEditText)).check(matches(withHint("Main Text")));
        onView(withId(R.id.edit_profile_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_profile_save_button)).check(matches(isDisplayed()));

        // Step 5: Input a string in the title field and check
        onView(withId(R.id.titleEditText)).perform(typeText("Sample Title"), closeSoftKeyboard());
        onView(withId(R.id.titleEditText)).check(matches(withText("Sample Title")));

        // Step 6: Input a string in the content field and check
        onView(withId(R.id.mainTextEditText)).perform(typeText("Sample Content"), closeSoftKeyboard());
        onView(withId(R.id.mainTextEditText)).check(matches(withText("Sample Content")));

        // Step 7: Click save
        onView(withId(R.id.edit_profile_save_button)).perform(click());

        // Step 8: Check for success toast (if applicable)
        // onView(isRoot()).check(matches(ToastMatcher.withToast("Post created successfully")));
    }

}
