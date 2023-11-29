package espressotest;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;
import org.hamcrest.Matcher;

public class ClickItemWithId implements ViewAction {
    private final int viewId;

    public ClickItemWithId(int viewId) {
        this.viewId = viewId;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isDisplayingAtLeast(90);  // At least 90% of the view must be displayed.
    }

    @Override
    public String getDescription() {
        return "Click on a child view with specified ID.";
    }

    @Override
    public void perform(UiController uiController, View view) {
        View v = view.findViewById(viewId);
        v.performClick();
    }
}
