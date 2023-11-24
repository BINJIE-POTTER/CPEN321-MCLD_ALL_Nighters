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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import com.example.cpen321mappost.MainActivity;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.PostActivity;
import com.example.cpen321mappost.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreatePostTest {
    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule = new ActivityScenarioRule<>(MapsActivity.class);

        @Before
        public void setUp() throws Exception {
            Intents.init();
        }

        @After
        public void tearDown() throws Exception {
            Intents.release();
        }

    @Test
    public void testCreatePostFlow() {
        // Verify that the current activity is MapsActivity
        intended(hasComponent(MapsActivity.class.getName()));

        // Step 1: Click on an empty area
        onView(withId(R.id.map)).perform(click());

        // Step 2: Check for bottom sheet dialog and its contents
        onView(withId(R.id.createPostButton)).check(matches(isDisplayed()));

        // Step 3: Click on “CREATE A POST” menu item
        onView(withId(R.id.createPostButton)).perform(click());
        intended(hasComponent(PostActivity.class.getName()));


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
        onView(withId(R.id.edit_profile_save_button)).perform(click());
        onView(isRoot()).check(matches(ToastMatcher.withToast("Please complete the content!")));

        // Step 6: Input a string in the content field and check
        onView(withId(R.id.mainTextEditText)).perform(typeText("Sample Content"), closeSoftKeyboard());
        onView(withId(R.id.mainTextEditText)).check(matches(withText("Sample Content")));

        // Step 7: Click save
        onView(withId(R.id.edit_profile_save_button)).perform(click());

        // Step 8: Check for success toast
        onView(isRoot()).check(matches(ToastMatcher.withToast("Post created successfully")));

        // Step 9: Check if returned to MapsActivity with new pin
        // Step 9: Check if MapsActivity is in the foreground
        intended(hasComponent(MapsActivity.class.getName()));

        // This might require checking the state of the MapsActivity or the map itself
    }


}
