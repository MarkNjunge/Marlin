package com.marknjunge.marlin.utils

import org.junit.Assert
import org.junit.Test

class GravatarTest {
    @Test
    fun should_generateAvatarUrl() {
        val result = Gravatar.generateAvatarUrl("mark.kamau@outlook.com")

        Assert.assertEquals("https://www.gravatar.com/avatar/b6d76dfe5ed8d065d80207a88c438d87?s=200", result)
    }
}