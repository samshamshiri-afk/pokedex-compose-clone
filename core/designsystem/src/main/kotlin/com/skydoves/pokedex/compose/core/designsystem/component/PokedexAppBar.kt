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

package com.skydoves.pokedex.compose.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.skydoves.pokedex.compose.core.designsystem.theme.PokedexTheme
import com.skydoves.pokedex.compose.designsystem.R

@Composable
fun PokedexAppBar(
  onSearchButtonClicked: () -> Unit = {},
  searchText: String = "",
  onSearchTextChanged: (String) -> Unit = {},
  isSearchActive: Boolean,
) {
  TopAppBar(
    title = {
      if (isSearchActive) {
        SearchBarTextField(
          searchText = searchText,
          onSearchTextChanged = onSearchTextChanged
        )
      } else {
        Text(
          text = stringResource(id = R.string.app_name),
          color = PokedexTheme.colors.absoluteWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
        )
      }
    },
    actions = {
      IconButton(
        onClick = onSearchButtonClicked
      ) {
        Icon(
          imageVector = if (isSearchActive) Icons.Default.Clear else Icons.Default.Search,
          contentDescription = if (isSearchActive) "Clear" else "Search",
          tint = PokedexTheme.colors.absoluteWhite,
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors().copy(
      containerColor = PokedexTheme.colors.primary,
    ),
  )
}

@Composable
fun SearchBarTextField(
  searchText: String,
  onSearchTextChanged: (String) -> Unit
) {
  TextField(
    value = searchText,
    onValueChange = onSearchTextChanged,
    placeholder = {
      Text(
        text = stringResource(id = R.string.search_pokemon),
        color = PokedexTheme.colors.absoluteWhite.copy(alpha = 0.6f)
      )
    },
    colors = TextFieldDefaults.colors(
      focusedContainerColor = PokedexTheme.colors.primary,
      unfocusedContainerColor = PokedexTheme.colors.primary,
      disabledContainerColor = PokedexTheme.colors.primary,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      cursorColor = PokedexTheme.colors.absoluteWhite,
      focusedTextColor = PokedexTheme.colors.absoluteWhite,
      unfocusedTextColor = PokedexTheme.colors.absoluteWhite,
      focusedPlaceholderColor = PokedexTheme.colors.absoluteWhite,
      unfocusedPlaceholderColor = PokedexTheme.colors.absoluteWhite,
    )
  )
}

@Preview
@Composable
private fun PokedexAppBarPreview() {
  PokedexTheme {
    PokedexAppBar(
      isSearchActive = false
    )
  }
}

@Preview
@Composable
private fun PokedexAppBarActiveSearchPreview() {
  PokedexTheme {
    PokedexAppBar(
      isSearchActive = true
    )
  }
}

