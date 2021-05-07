package com.example.lab7_3


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.containsString

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val rule = activityScenarioRule<MainActivity>()


    @Test
    fun broadcastTest() {
        val scenario = rule.scenario
        Espresso.onView(ViewMatchers.withId(R.id.text))
            .check(ViewAssertions.matches(ViewMatchers.withText("Hello World!")))
//        Espresso.onView(ViewMatchers.withId(R.id.button)).perform(ViewActions.click())
//        Espresso.onView(ViewMatchers.withId(R.id.text))
//            .check(ViewAssertions.matches(ViewMatchers.withText(containsString("/"))))
    }
}
