package com.example.user_pc.withtabs;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.service.autofill.Validator;
import android.support.design.widget.TabLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import static android.service.autofill.Validators.not;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static java.util.regex.Pattern.matches;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class Tab1FragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule(MainActivity.class);

    @Rule
    public IntentsTestRule<MainActivity> activityRule1 =
            new IntentsTestRule<>(MainActivity.class);

    private MainActivity mAct;


    @Before
    public void setUp() throws Exception {
        mAct = activityRule.getActivity();
        //activityRule.getActivity()
          //      .getSupportFragmentManager().beginTransaction();

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.user_pc.withtabs", appContext.getPackageName());
    }

    @Test
    public void testviewPAger(){
        ViewPager viewPager = mAct.findViewById(R.id.viewPager);
        assertNotNull(viewPager);
    }
    @Test
    public void testImageBTN(){
        Button bt = mAct.findViewById(R.id.img);
        assertNotNull(bt);
    }
    @Test
    public void testCropBTN(){
        Button bt2 = mAct.findViewById(R.id.cropbtn);
        assertNotNull(bt2);
    }

    @Test
    public void takePhoto_drawableIsApplied() {

        // Click on the button that will trigger the stubbed intent.
       // onView(withId(R.id.img)).perform((ViewAction) click());

        // With no user interaction, the ImageView will have a drawable.
        //onView(withId(R.id.imageView)).check(matches(hasDrawable()));
    }









}