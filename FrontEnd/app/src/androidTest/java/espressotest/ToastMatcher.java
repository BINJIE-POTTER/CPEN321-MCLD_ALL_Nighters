package espressotest;
import android.view.View;
import android.widget.TextView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<View> {

    private final String expectedText;

    public ToastMatcher(String expectedText) {
        this.expectedText = expectedText;
    }

    @Override
    protected boolean matchesSafely(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            return textView.getText().toString().equals(expectedText);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with toast text: " + expectedText);
    }

    public static Matcher<View> withToast(String text) {
        return new ToastMatcher(text);
    }
}
