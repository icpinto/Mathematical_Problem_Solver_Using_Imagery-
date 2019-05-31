package com.example.user_pc.withtabs;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UITesting {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void uITesting() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.img), withText("Load Image"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.img),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.img), withText("Load Image"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction tabView = onView(
                allOf(withContentDescription("Solution"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabs),
                                        0),
                                1),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.viewPager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction tabView2 = onView(
                allOf(withContentDescription("Solution"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabs),
                                        0),
                                1),
                        isDisplayed()));
        tabView2.perform(click());

        ViewInteraction scrollView = onView(
                allOf(withId(R.id.scrollView2),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                1),
                        isDisplayed()));
//        scrollView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.textView), withText("Result"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
//        textView.check(matches(withText("Result")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView), withText("Result"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
//        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textView), withText("Result"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
//        textView3.check(matches(isDisplayed()));

        ViewInteraction tabView3 = onView(
                allOf(withContentDescription("History"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabs),
                                        0),
                                2),
                        isDisplayed()));
        tabView3.perform(click());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.viewPager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        viewPager2.perform(swipeLeft());

        ViewInteraction textView4 = onView(
                allOf(withText("History"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("History")));

        ViewInteraction textView5 = onView(
                allOf(withText("History"),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                0),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.deltebtn),
                        childAtPosition(
                                withParent(withId(R.id.viewPager)),
                                2),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                allOf(withParent(allOf(withId(R.id.viewPager),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                1))),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
                allOf(withParent(allOf(withId(R.id.viewPager),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                1))),
                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
