package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.HoneypotParameterResult
import com.example.data.model.LiquidityLockParameterResult
import com.example.data.model.RiskLevel
import com.example.data.model.SecurityReport
import com.example.data.model.TaxParameterResult
import com.example.data.model.WarningAlert
import com.example.ui.components.RedWarningAlertBanner
import com.example.ui.components.SafetyScoreGauge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
        ) {
          Column {
            SafetyScoreGauge(score = 88, riskLevel = RiskLevel.SAFE)
            RedWarningAlertBanner(
              alerts = listOf(
                WarningAlert(
                  title = "CRITICAL: HONEYPOT DETECTED",
                  description = "This token restricts selling.",
                  isCritical = true
                )
              )
            )
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
