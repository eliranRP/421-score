package com.eliranrp.score421

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliranrp.score421.ui.FeuilleViewModel
import com.eliranrp.score421.ui.Score421App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = (application as Score421Application).namesStore
        setContent {
            val viewModel: FeuilleViewModel = viewModel(factory = FeuilleViewModel.factory(store))
            Score421App(viewModel)
        }
    }
}
