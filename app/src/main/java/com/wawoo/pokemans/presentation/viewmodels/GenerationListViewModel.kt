package com.wawoo.pokemans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wawoo.pokemans.data.models.Generation
import com.wawoo.pokemans.data.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GenerationListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerationListUiState())
    val uiState: StateFlow<GenerationListUiState> = _uiState.asStateFlow()

    init {
        loadGenerations()
    }

    private fun loadGenerations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Check if we have initial data
            val hasData = repository.hasInitialData()
            
            if (!hasData) {
                // First run - need internet connection
                try {
                    repository.refreshGenerations()
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Internet connection required for first run. Please check your connection and try again."
                    )
                    return@launch
                }
            }
            
            repository.getAllGenerations()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
                .collect { generations ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generations = generations,
                        error = null
                    )
                    
                    // If still no generations after trying to refresh, show error
                    if (generations.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            error = "Internet connection required for first run. Please check your connection and try again."
                        )
                    }
                }
        }
    }

    fun refreshGenerations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                repository.refreshGenerations()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun onGenerationClick(generation: Generation) {
        // Navigation will be handled in the UI layer
    }
}

data class GenerationListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val generations: List<Generation> = emptyList(),
    val error: String? = null
)

class GenerationListViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerationListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerationListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}