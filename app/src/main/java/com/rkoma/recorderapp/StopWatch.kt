package com.rkoma.recorderapp

import android.widget.TextView
import kotlin.concurrent.fixedRateTimer
import java.util.Timer

class StopWatch(private val Time: TextView){

    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isRunning: Boolean = false
    private var timer: Timer? = null

    // Avvia il cronometro
    fun startSw() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            timer = fixedRateTimer(initialDelay = 0, period = 100) {
                val elapsedTimeStr = getElapsedTimeString()
                Time.post {
                    Time.text = elapsedTimeStr
                }
            }
            isRunning = true
        }
    }

    // Pausa il cronometro
    fun pauseSw() {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime
            timer?.cancel()
            isRunning = false
        }
    }

    // Resetta il cronometro
    fun resetSw() {
        timer?.cancel()
        startTime = 0
        elapsedTime = 0
        isRunning = false
        //Time.text = "00:00:00"
    }

    // Ottiene il tempo trascorso in millisecondi
    fun getElapsedTime(): Long {
        return if (isRunning) {
            System.currentTimeMillis() - startTime
        } else {
            elapsedTime
        }
    }

    // Ottiene il tempo trascorso in formato mm:ss
    fun getElapsedTimeString(): String {
        val totalSeconds = getElapsedTime() / 1000
        val millisecond= getElapsedTime() % 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val hour =  totalSeconds / (60 * 60)
        if (hour > 0)
            return String.format("%02d:%02d:%02d.%02d", hour, minutes, seconds, millisecond/10)
        else
            return String.format("%02d:%02d.%02d", minutes, seconds, millisecond/10)

    }


}
