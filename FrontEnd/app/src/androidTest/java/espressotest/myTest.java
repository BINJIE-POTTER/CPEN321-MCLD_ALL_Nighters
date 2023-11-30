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


import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.widget.Button;
import android.widget.EditText;

import com.example.cpen321mappost.AuthenticationActivity;
import com.example.cpen321mappost.MapsActivity;
import com.example.cpen321mappost.PostActivity;
import com.example.cpen321mappost.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class myTest {

    private CountingIdlingResource idlingResource = new CountingIdlingResource("DATA_LOADER");

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> activityScenarioRule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION // If needed
    );
    private UiDevice device;


    @Before
    public void setUp() throws Exception {
        Intents.init();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    @Test
    public void test() throws UiObjectNotFoundException {


        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(withId(R.id.btnGoogleSignIn)).perform(click());
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.increment();
        idlingResource.decrement();
        // Wait for the Google Sign-In account chooser
        UiObject addAccount = device.findObject(new UiSelector()
                .textContains("Add another account")
                .className("android.widget.TextView"));

        if (addAccount.waitForExists(5000)) {
            addAccount.click();

            UiObject emailOrPhoneField = device.findObject(new UiSelector()
                    .className("android.widget.EditText"));

            if (emailOrPhoneField.waitForExists(200000)) {
                emailOrPhoneField.click();
                try {
                    Thread.sleep(3000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                emailOrPhoneField.setText("changhuanfei@gmail.com");
                try {
                    Thread.sleep(5000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                UiObject nextButton = device.findObject(new UiSelector()
                        .textContains("Next")
                        .className(Button.class.getName()));
                nextButton.click();

                // Wait for the password field
                UiObject passwordField = device.findObject(new UiSelector()
                        .className("android.widget.EditText"));


                if (passwordField.waitForExists(50000)) {
                    passwordField.setText("o12326686");
                    try {
                        Thread.sleep(3000); // Wait for 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UiObject signInButton = device.findObject(new UiSelector()
                            .textContains("Next") // The button text might be different, like "Sign In"
                            .className(Button.class.getName()));
                    signInButton.click();
                    try {
                        Thread.sleep(3000); // Wait for 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Add assertions or further actions as needed
                } else {
                    fail("Password field did not appear.");
                }
            } else {
                fail("Sign-in dialog did not appear.");
            }
            //


        } //if no user existed before
        else
        {

        }


        // Step 1: Click on an empty area
        onView(withId(R.id.map)).perform(click());

        // Step 2: Check for bottom sheet dialog and its contents
        onView(withId(R.id.createPostButton)).check(matches(isDisplayed()));

        // Step 3: Click on “CREATE A POST” menu item
        onView(withId(R.id.createPostButton)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

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


        // Step 5: Input a string in the title field and check
        onView(withId(R.id.titleEditText)).perform(typeText("Sample Title"), closeSoftKeyboard());
        onView(withId(R.id.titleEditText)).check(matches(withText("Sample Title")));


        // Step 6: Input a string in the content field and check
        onView(withId(R.id.mainTextEditText)).perform(typeText("Sample Content"), closeSoftKeyboard());
        onView(withId(R.id.mainTextEditText)).check(matches(withText("Sample Content")));

        // Step 7: Click save
        onView(withId(R.id.edit_profile_save_button)).perform(click());

        // Step 8: Check for success toast
//        onView(isRoot()).check(matches(ToastMatcher.withToast("Post created successfully")));

    }


}
