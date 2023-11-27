package espressotest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
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
import com.example.cpen321mappost.PostPreviewListActivity;
import com.example.cpen321mappost.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.example.cpen321mappost.SearchActivity;
import com.example.cpen321mappost.User;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchAndFilterPostTest {
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
        Intents.init();

    }
    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    @Test
    public void testSearchFilterFlow() {

        idlingResource.increment();
        idlingResource.decrement();
        // Click the moreButton
        onView(withId(R.id.moreButton)).perform(click());

        // Wait for the PopupMenu to appear and click "Search"
        onView(withText("Search")).inRoot(isPlatformPopup()).perform(click());

        // Wait for SearchActivity to load
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Verify that the app navigates to the SearchActivity
        intended(hasComponent(SearchActivity.class.getName()));

        // Type "s" into the search EditText
        onView(withId(R.id.search_edit_text)).perform(typeText("s"));

        // Click the search button
        User.TEST_MODE=true;

        onView(withId(R.id.search_button)).perform(click());

        // Optional: Verify that the app navigates to PostPreviewListActivity with the correct search string
         intended(hasComponent(PostPreviewListActivity.class.getName()));

        intended(hasExtra("searchString", "s"));
        idlingResource.increment(); // Before starting the background task
        idlingResource.decrement(); // After completing the background task

        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
