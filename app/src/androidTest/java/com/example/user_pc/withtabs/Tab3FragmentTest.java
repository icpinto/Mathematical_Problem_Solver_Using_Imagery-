package com.example.user_pc.withtabs;

import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.filters.SmallTest;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static java.lang.System.in;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

public class Tab3FragmentTest {
    private MainActivity mAct;
    private ListView mListView;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp() throws Exception {

        mAct = activityRule.getActivity();
        mListView = (ListView) mAct.findViewById(R.id.list);


    }
    @Test
    public void testListView(){

    }


}