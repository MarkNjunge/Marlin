package com.marknjunge.marlin.data.network

interface DigitalOceanConfig{
    val clientId:String
    val clientSecret:String
    val redirectUrl:String
}

class DigitalOceanConfigImpl(override val clientId: String, override val clientSecret: String, override val redirectUrl: String) : DigitalOceanConfig