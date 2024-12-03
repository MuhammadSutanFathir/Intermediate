package com.example.submissionintermediate.view.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.submissionintermediate.R
import com.example.submissionintermediate.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        onView(withId(R.id.ed_login_email))
            .perform(typeText("valid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password))
            .perform(typeText("correct_password"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailed() {
        onView(withId(R.id.ed_login_email))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password))
            .perform(typeText("wrong_password"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

    }
}