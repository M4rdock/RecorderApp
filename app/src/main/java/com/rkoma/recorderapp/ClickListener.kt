package com.rkoma.recorderapp

interface ClickListener {
    abstract val filePath: String

    fun clicklistener(position : Int)
    fun longclicklistener(position: Int)
}