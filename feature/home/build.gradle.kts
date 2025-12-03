plugins {
  id("skydoves.pokedex.android.feature")
  id("skydoves.pokedex.android.hilt")
}

android {
  namespace = "com.skydoves.pokedex.compose.feature.home"
}

dependencies {
  api(projects.core.model)
  testImplementation(projects.core.test)
  testImplementation(libs.kotlinx.coroutines.test)

  // unit test
  testImplementation(libs.junit)
  testImplementation(libs.turbine)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
}
