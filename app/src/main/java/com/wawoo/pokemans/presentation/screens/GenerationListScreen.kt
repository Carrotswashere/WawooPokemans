package com.wawoo.pokemans.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wawoo.pokemans.data.models.Generation
import com.wawoo.pokemans.presentation.viewmodels.GenerationListViewModel
import com.wawoo.pokemans.presentation.viewmodels.GenerationListViewModelFactory
import com.wawoo.pokemans.utils.GenerationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerationListScreen(
    onGenerationClick: (Generation) -> Unit,
    viewModelFactory: GenerationListViewModelFactory,
    modifier: Modifier = Modifier
) {
    val viewModel: GenerationListViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Pokémon Generations") },
            actions = {
                IconButton(
                    onClick = { viewModel.refreshGenerations() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.error != null -> {
                val errorMessage = uiState.error!!
                if (errorMessage.contains("Internet connection required")) {
                    NetworkRequiredScreen(
                        message = errorMessage,
                        onRetry = { viewModel.refreshGenerations() }
                    )
                } else {
                    ErrorScreen(
                        error = errorMessage,
                        onRetry = { viewModel.refreshGenerations() }
                    )
                }
            }
            uiState.generations.isEmpty() -> {
                EmptyScreen("No generations found")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.generations,
                        key = { generation -> generation.id }
                    ) { generation ->
                        GenerationCard(
                            generation = generation,
                            onClick = { onGenerationClick(generation) }
                        )
                    }
                }
            }
        }

        if (uiState.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GenerationCard(
    generation: Generation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = GenerationUtils.formatGenerationName(generation.name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            val pokemonCountText = GenerationUtils.formatPokemonCount(generation.pokemonCount)
            if (pokemonCountText.isNotEmpty()) {
                Text(
                    text = pokemonCountText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}