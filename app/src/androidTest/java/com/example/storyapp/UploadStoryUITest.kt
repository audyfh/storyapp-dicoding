package com.example.storyapp


import android.content.Context
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.data.network.api.ApiService
import com.example.storyapp.presentation.add.AddActivity
import com.example.storyapp.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import org.hamcrest.Matcher

@RunWith(AndroidJUnit4::class)
class UploadStoryUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(0)
        ApiService.BASE_URL = mockWebServer.url("/").toString()

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        mockWebServer.shutdown()
    }

    @Test
    fun uploadStory_andStoryAppearsInHome() {
        val description = "Buat UI Test"

        val uploadResponse = """
        {
          "error": false,
          "message": "Story uploaded successfully"
        }
    """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(uploadResponse)
        )

        val storyListResponse = """
        {
          "error": false,
          "message": "Stories fetched",
          "listStory": [
            {
              "id": "story-123",
              "name": "abitest",
              "description": "$description",
              "photoUrl": "https://dummyimage.com/story.jpg",
              "createdAt": "2025-08-16T12:00:00Z"
            }
          ]
        }
    """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(storyListResponse)
        )

        // --- FLOW TEST ---
        val scenario = ActivityScenario.launch(AddActivity::class.java)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dummyFile = File(context.cacheDir, "dummy.jpg")
        dummyFile.writeBytes(ByteArray(10))

        scenario.onActivity { activity ->
            activity.setDummyImageFile(dummyFile)
        }

        onView(withId(R.id.etDescription))
            .perform(typeText(description), closeSoftKeyboard())

        onView(withId(R.id.btnUpload)).perform(click())

        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.rvStory)).check(matches(isDisplayed()))
    }

}


fun waitFor(millis: Long): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()
        override fun getDescription(): String = "Wait for $millis milliseconds."
        override fun perform(uiController: UiController, view: View?) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    }
}
