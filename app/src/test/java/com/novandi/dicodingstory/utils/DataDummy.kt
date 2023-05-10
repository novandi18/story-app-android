package com.novandi.dicodingstory.utils

import com.novandi.dicodingstory.api.StoryItems

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryItems> {
        val storyList: MutableList<StoryItems> = arrayListOf()
        for (i in 0..5) {
            val stories = StoryItems(
                "id-$i",
                "Title $i",
                "Description $i",
                "https://story-api.dicoding.dev/images/stories/photos-1683615858411_OkjiCYL1.jpg",
                "2023-05-09T07:04:18.412Z"
            )
            storyList.add(stories)
        }

        return storyList
    }
}