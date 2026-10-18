package com.hackpath.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hackpath.app.audio.AudioEngine
import com.hackpath.app.ui.HackPathNavGraph
import com.hackpath.app.ui.theme.HackPathTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var audioEngine: AudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackPathTheme {
                HackPathNavGraph()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.destroy()
    }
}
