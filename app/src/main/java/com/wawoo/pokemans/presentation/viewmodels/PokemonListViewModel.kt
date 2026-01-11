package com.wawoo.pokemans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wawoo.pokemans.data.models.Pokemon
import com.wawoo.pokemans.data.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val repository: PokemonRepository,
    private val generationId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    init {
        loadPokemon()
    }

    private fun loadPokemon() {
        // Start observing the pokemon list immediately
        viewModelScope.launch {
            repository.getPokemonByGeneration(generationId)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = exception.message
                    )
                }
                .collect { pokemonList ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        pokemonList = pokemonList,
                        error = null
                    )
                }
        }
        
        // Check if we need to fetch data and start background loading
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val hasData = repository.hasPokemonForGeneration(generationId)
            
            if (!hasData) {
                // First time loading this generation - need internet
                // Switch to refreshing state once we start fetching
                _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = true)
                
                try {
                    repository.refreshPokemonForGeneration(generationId)
                    // Loading complete
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        hasLoadedAll = true
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Internet connection required to load Pokemon data. Please check your connection and try again."
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLoadedAll = true
                )
            }
        }
    }

    fun refreshPokemon() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                repository.refreshPokemonForGeneration(generationId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun onPokemonClick(pokemon: Pokemon) {
        // Navigation will be handled in the UI layer
    }
}

data class PokemonListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val pokemonList: List<Pokemon> = emptyList(),
    val error: String? = null,
    val hasLoadedAll: Boolean = false
)

class PokemonListViewModelFactory(
    private val repository: PokemonRepository,
    private val generationId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonListViewModel(repository, generationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}