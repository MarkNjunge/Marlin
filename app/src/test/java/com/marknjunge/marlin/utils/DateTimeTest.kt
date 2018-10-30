package com.marknjunge.marlin.utils

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*


class DateTimeTest {

    @Before
    fun setup(){
        // Tests are configured based on GMT +3
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Nairobi"))
    }

    @Test
    fun should_convertFromDate(){
        val dateTime = Date(1514784600L * 1000).toDateTime()

        Assert.assertEquals(2018, dateTime.year)
        Assert.assertEquals(1, dateTime.month)
        Assert.assertEquals(1, dateTime.dayOfMonth)
        Assert.assertEquals(8, dateTime.hourOfDay)
        Assert.assertEquals(30, dateTime.minute)
        Assert.assertEquals(0, dateTime.second)
    }

    @Test
    fun should_convertToTimestamp() {
        val dateTime = DateTime(2018, 1, 1, 8, 30, 0)
        Assert.assertEquals(1514784600, dateTime.timestamp)
    }

    @Test
    fun should_convertFromTimestamp() {
        val dateTime = DateTime.fromTimestamp(1514784600)

        Assert.assertEquals(2018, dateTime.year)
        Assert.assertEquals(1, dateTime.month)
        Assert.assertEquals(1, dateTime.dayOfMonth)
        Assert.assertEquals(8, dateTime.hourOfDay)
        Assert.assertEquals(30, dateTime.minute)
        Assert.assertEquals(0, dateTime.second)
    }

    @Test
    fun should_format() {
        val dateTime = DateTime(2001, 7, 4, 12, 8, 56)

        Assert.assertEquals("2001.07.04 AD at 12:08:56", dateTime.format("yyyy.MM.dd G 'at' HH:mm:ss"))
    }

    @Test
    fun should_getNow() {
        Assert.assertEquals(System.currentTimeMillis() / 1000, DateTime.now.timestamp)
    }
}