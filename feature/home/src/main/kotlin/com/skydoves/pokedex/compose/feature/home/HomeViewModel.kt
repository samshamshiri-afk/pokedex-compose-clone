/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.pokedex.compose.feature.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.skydoves.pokedex.compose.core.data.repository.home.HomeRepository
import com.skydoves.pokedex.compose.core.model.Pokemon
import com.skydoves.pokedex.compose.core.viewmodel.BaseViewModel
import com.skydoves.pokedex.compose.core.viewmodel.ViewModelStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val homeRepository: HomeRepository,
) : BaseViewModel() {

  internal val uiState: ViewModelStateFlow<HomeUiState> = viewModelStateFlow(HomeUiState.Loading)
  private var isLastPageReached = false

  var isSearchActive by mutableStateOf(false)
    private set

  var searchQuery by mutableStateOf("")
    private set
  private val _searchQueryFlow = MutableStateFlow("")

  fun toggleSearchActive() {
    isSearchActive = !isSearchActive
    if (!isSearchActive) {
      searchQuery = ""
      pokemonFetchingIndex.value = 0
    }
  }

  fun updateSearchQuery(query: String) {
    searchQuery = query
    _searchQueryFlow.value = query
    if (query.isBlank()) {
      pokemonFetchingIndex.value = 0
      isLastPageReached = false
    }
  }

  private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)

  val pokemonList: StateFlow<List<Pokemon>> = combine(
    pokemonFetchingIndex,
    _searchQueryFlow
  ) { page, query ->
    page to query
  }.flatMapLatest { (page, query) ->
    homeRepository.fetchPokemonList(
      page = page,
      onStart = { uiState.tryEmit(key, HomeUiState.Loading) },
      onComplete = { uiState.tryEmit(key, HomeUiState.Idle) },
      onLastPageReached = { isLastPageReached = true },
      onError = { uiState.tryEmit(key, HomeUiState.Error(it)) },
    ).map { list ->
            if (query.isBlank()) {
        list
      } else {
        list.filter { it.name.contains(query, ignoreCase = true) }
      }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = emptyList(),
  )

  fun fetchNextPokemonList() {
    if (uiState.value != HomeUiState.Loading && !isLastPageReached && searchQuery.isBlank()) {
      pokemonFetchingIndex.value++
    }
  }
}

@Stable
internal sealed interface HomeUiState {

  data object Idle : HomeUiState

  data object Loading : HomeUiState

  data class Error(val message: String?) : HomeUiState
}
