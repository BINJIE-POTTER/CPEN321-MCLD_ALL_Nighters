package espressotest;
import static androidx.core.util.Predicate.not;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;


import android.content.Context;

import androidx.test.core.app.ActivityScenario;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import com.example.cpen321mappost.AuthenticationActivity;
import com.example.cpen321mappost.MainActivity;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.PostActivity;
import com.example.cpen321mappost.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreatePostTest {


    @Rule
    public ActivityScenarioRule<MapsActivity> activityScenarioRule = new ActivityScenarioRule<>(MapsActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION // If needed
    );

    private ActivityScenario<AuthenticationActivity> activityScenario;

        @Before
        public void setUp() throws Exception {
            // Set the test mode flag before launching the activity
            AuthenticationActivity.TEST_MODE = true;

            // Manually launch the activity

            // Initialize Intents for Espresso-Intents
            Intents.init();
//            activityScenario = ActivityScenario.launch(AuthenticationActivity.class);

        }

        @After
        public void tearDown() throws Exception {
            AuthenticationActivity.TEST_MODE = false;

            Intents.release();
        }

    @Test
    public void testCreatePostFlow() {
        try {
            Thread.sleep(10000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Verify that the current activity is MapsActivity
//        intended(hasComponent(MapsActivity.class.getName()));

        // Step 1: Click on an empty area
        onView(withId(R.id.map)).perform(click());

        // Step 2: Check for bottom sheet dialog and its contents
        onView(withId(R.id.createPostButton)).check(matches(isDisplayed()));

        // Step 3: Click on “CREATE A POST” menu item
        onView(withId(R.id.createPostButton)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

// Wait for the permission dialog to appear and click the allow button.
        uiDevice.wait(Until.hasObject(By.text("Allow")), 3000);
        uiDevice.findObject(By.text("Allow")).click();
        intended(hasComponent(PostActivity.class.getName()));
        PostActivity.TEST_MODE = true;


        // Step 4: Check UI elements in PostActivity
        onView(withId(R.id.imgPreview)).check(matches(isDisplayed()));
        onView(withId(R.id.titleEditText)).check(matches(withHint("Title")));
        onView(withId(R.id.mainTextEditText)).check(matches(withHint("Main Text")));
        onView(withId(R.id.edit_profile_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_profile_save_button)).check(matches(isDisplayed()));


        // Step 4a: Check for permission dialog
        // This might require a different approach as system dialogs are not part of the app's UI

        // Step 5: Input a string in the title field and check
        onView(withId(R.id.titleEditText)).perform(typeText("Sample Title"), closeSoftKeyboard());
        onView(withId(R.id.titleEditText)).check(matches(withText("Sample Title")));


        // Step 5a and 5a1: Click save and check for error toast
//        onView(withId(R.id.edit_profile_save_button)).perform(click());
//        onView(isRoot()).check(matches(ToastMatcher.withToast("Please complete the content!")));

        // Step 6: Input a string in the content field and check
        onView(withId(R.id.mainTextEditText)).perform(typeText("Sample Content"), closeSoftKeyboard());
        onView(withId(R.id.mainTextEditText)).check(matches(withText("Sample Content")));

        // Step 7: Click save
        onView(withId(R.id.edit_profile_save_button)).perform(click());

        // Step 8: Check for success toast
//        onView(isRoot()).check(matches(ToastMatcher.withToast("Post created successfully")));

    }


}
