package com.wawoo.pokemans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wawoo.pokemans.navigation.PokemonNavigation
import com.wawoo.pokemans.ui.theme.PokemansTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val application = application as PokemonApplication
        
        setContent {
            PokemansTheme {
                PokemonApp(application = application)
            }
        }
    }
}

@Composable
fun PokemonApp(application: PokemonApplication) {
    val navController = rememberNavController()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        PokemonNavigation(
            navController = navController,
            repository = application.repository,
            modifier = Modifier.padding(innerPadding)
        )
    }
}