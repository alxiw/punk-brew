package io.github.alxiw.punkbrew.util

object Extensions {

    fun Double.format(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()
}
