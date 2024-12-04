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
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        // Initialize MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Register IdlingResource to sync with Espresso
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Set up mock URL for Retrofit (or other network client)
        val baseUrl = mockWebServer.url("/").toString()
        // Update your Retrofit or network client here to use the mockWebServer URL
        // Example: RetrofitInstance.baseUrl = baseUrl
    }

    @After
    fun tearDown() {
        // Shut down the mock server after each test
        mockWebServer.shutdown()

        // Unregister IdlingResource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        // Mock a successful login response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"message\": \"Login successful\"}")
        mockWebServer.enqueue(mockResponse)

        // Perform the login actions
        onView(withId(R.id.ed_login_email))
            .perform(typeText("valid@example.com"))
        onView(withId(R.id.ed_login_password))
            .perform(typeText("correct_password"))

        onView(withId(R.id.login_button)).perform(click())

        // Verify that MainActivity is displayed after login
        onView(withId(R.id.main_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailed() {
        // Mock a failed login response
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("{\"message\": \"Invalid credentials\"}")
        mockWebServer.enqueue(mockResponse)

        // Perform the login actions
        onView(withId(R.id.ed_login_email))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password))
            .perform(typeText("wrong_password"), closeSoftKeyboard())

        onView(withId(R.id.login_button)).perform(click())

        // Verify that the error message is shown (you can customize this check)
        onView(withText("Invalid credentials")).check(matches(isDisplayed()))
    }
}
