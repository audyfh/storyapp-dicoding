package com.example.storyapp

import java.util.concurrent.TimeUnit
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.storyapp.data.local.entity.StoryEntity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set")
        }
    } finally {
        this.removeObserver(observer)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

object DataDummy{
    fun generateDummyStories() : List<StoryEntity> {
        val listStory : MutableList<StoryEntity> = arrayListOf()
        for (i in 1..10){
            val story = StoryEntity(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658",
                createdAt = "2022-01-08T06:34:18.598Z",
                description = "Lorem ipsum",
                lat = 12.0,
                lon = 12.0,
                name = "UserTest"
            )
            listStory.add(story)
        }
        return listStory
    }
}