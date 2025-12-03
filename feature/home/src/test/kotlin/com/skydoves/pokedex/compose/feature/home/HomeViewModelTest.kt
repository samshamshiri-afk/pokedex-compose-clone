package com.skydoves.pokedex.compose.feature.home

import com.skydoves.pokedex.compose.core.data.repository.home.HomeRepository
import com.skydoves.pokedex.compose.core.model.Pokemon
import com.skydoves.pokedex.compose.core.test.MainCoroutinesRule
import com.skydoves.pokedex.compose.core.test.MockUtil.mockPokemonList
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class HomeViewModelTest {

  private lateinit var viewModel: HomeViewModel
  private lateinit var homeRepository: HomeRepository

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  private val mockPokemonList = mockPokemonList()

  @Before
  fun setup() {
    homeRepository = mock()

    // Mock repository to return pokemon list
    whenever(
      homeRepository.fetchPokemonList(
        page = any(),
        onStart = any(),
        onComplete = any(),
        onLastPageReached = any(),
        onError = any()
      )
    ).thenAnswer { invocation ->
      val onStart = invocation.getArgument<() -> Unit>(1)
      val onComplete = invocation.getArgument<() -> Unit>(2)

      onStart.invoke()
      flowOf(mockPokemonList).also {
        onComplete.invoke()
      }
    }

    viewModel = HomeViewModel(homeRepository)
  }

  @Test
  fun `initial state should have search inactive and empty query`() {
    assertFalse(viewModel.isSearchActive)
    assertEquals("", viewModel.searchQuery)
  }

  @Test
  fun `toggleSearchActive should activate search`() {
    viewModel.toggleSearchActive()
    assertTrue(viewModel.isSearchActive)
  }

  @Test
  fun `updateSearchQuery should update query`() {
    viewModel.updateSearchQuery("New Query")
    assertEquals("New Query", viewModel.searchQuery)
  }

  @Test
    fun `toggleSearchActive twice should deactivate search and clear query`() {
    //Activate Search
    viewModel.toggleSearchActive()
    viewModel.updateSearchQuery("New Query")
    assertTrue(viewModel.isSearchActive)
    assertEquals("New Query", viewModel.searchQuery)

    //Deactivate Search
    viewModel.toggleSearchActive()
    assertFalse(viewModel.isSearchActive)
    assertEquals("", viewModel.searchQuery)
  }

  @Test
  fun `search should filter pokemon by name case insensitive`() = runTest {
    val results = mutableListOf<List<Pokemon>>()

    // Start collecting the flow in background
    val job = launch {
      viewModel.pokemonList.collect {
        results.add(it)
      }
    }

    // Wait for initial emission
    advanceUntilIdle()
    assertTrue("Should have initial emission", results.isNotEmpty())

    // Update search query
    viewModel.updateSearchQuery("bulba")

    // Wait for debounce and processing
    advanceTimeBy(350)
    advanceUntilIdle()

    // Get the latest result
    val filtered = results.last()
    assertTrue("Filtered list should not be empty", filtered.isNotEmpty())
    assertTrue(
      "All items should contain 'bulba'",
      filtered.all { it.name.contains("bulba", ignoreCase = true) }
    )

    job.cancel()
  }
}