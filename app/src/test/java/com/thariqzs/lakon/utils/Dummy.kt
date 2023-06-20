package com.thariqzs.lakon.utils

import com.thariqzs.lakon.data.model.Story
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

object StoryDataDummy {
    fun generateDummyNewsEntity(): List<Story> {
        val dateTimeFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .appendValue(ChronoField.MILLI_OF_SECOND, 3)
            .appendPattern("'Z'")
            .toFormatter()

        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(dateTimeFormatter)
        val storyList = ArrayList<Story>()
        for (i in 0..50) {
            val story = Story(
                "id_$i",
                "name_$i",
                formattedDateTime,
                "photo_num_$i",
                "description_of_pic_$i",
                (-10.212).toFloat(),
                (-16.002).toFloat()
            )
            storyList.add(story)
        }

        return storyList
    }
}