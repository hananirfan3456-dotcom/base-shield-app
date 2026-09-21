package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RiskLevel
import com.example.data.model.SecurityReport
import com.example.data.model.TokenSecurityRaw
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Base Shield Scanner", appName)
  }

  @Test
  fun `test honeypot detection triggers zero score and critical alert`() {
    val raw = TokenSecurityRaw(
      tokenName = "Scam Token",
      tokenSymbol = "SCAM",
      isHoneypot = "1",
      cannotBuy = "0",
      cannotSellAll = "1",
      isInDex = "1",
      buyTax = "0.05",
      sellTax = "0.05"
    )

    val report = SecurityReport.evaluate("0x1111111111111111111111111111111111111111", raw)

    // Honeypot should fail parameter 1 and yield 0 for honeypot score
    assertEquals(0, report.honeypotResult.score)
    assertFalse(report.honeypotResult.passed)
    assertTrue(report.warningAlerts.any { it.title.contains("HONEYPOT") && it.isCritical })
    assertEquals(RiskLevel.HIGH_RISK, report.riskLevel)
  }

  @Test
  fun `test safe token yields high safety score`() {
    val raw = TokenSecurityRaw(
      tokenName = "Safe Base Token",
      tokenSymbol = "BASE",
      isHoneypot = "0",
      cannotBuy = "0",
      cannotSellAll = "0",
      isInDex = "1",
      lpHolders = listOf(
        com.example.data.model.LpHolderRaw(
          address = "0x000000000000000000000000000000000000dead",
          percent = "0.99",
          isLocked = 1,
          tag = "Burned"
        )
      ),
      buyTax = "0.00",
      sellTax = "0.00",
      slippageModifiable = "0",
      isOpenSource = "1",
      isMintable = "0",
      isProxy = "0"
    )

    val report = SecurityReport.evaluate("0x2222222222222222222222222222222222222222", raw)

    assertEquals(40, report.honeypotResult.score)
    assertTrue(report.honeypotResult.passed)
    assertEquals(30, report.liquidityLockResult.score)
    assertTrue(report.liquidityLockResult.isLockedAcceptable)
    assertEquals(30, report.taxResult.score)
    assertFalse(report.taxResult.isExcessive)
    assertTrue(report.warningAlerts.none { it.isCritical })
    assertEquals(100, report.safetyScore)
    assertEquals(RiskLevel.SAFE, report.riskLevel)
  }

  @Test
  fun `test excessive taxes trigger warning alert`() {
    val raw = TokenSecurityRaw(
      tokenName = "High Tax Token",
      tokenSymbol = "TAX",
      isHoneypot = "0",
      isInDex = "1",
      buyTax = "0.20", // 20%
      sellTax = "0.25", // 25%
      slippageModifiable = "1"
    )

    val report = SecurityReport.evaluate("0x3333333333333333333333333333333333333333", raw)

    assertTrue(report.taxResult.isExcessive)
    assertTrue(report.taxResult.isModifiable)
    assertTrue(report.warningAlerts.any { it.title.contains("EXCESSIVE TRADING TAX") })
  }

  @Test
  fun `test liquidity locked yields safe explicit english text warning`() {
    val raw = TokenSecurityRaw(
      tokenName = "Locked Token",
      tokenSymbol = "LCK",
      isHoneypot = "0",
      isInDex = "1",
      lpLocked = "1",
      buyTax = "0.00",
      sellTax = "0.00"
    )

    val report = SecurityReport.evaluate("0x4444444444444444444444444444444444444444", raw)

    assertTrue(report.liquidityLockResult.isLocked)
    assertEquals(
      "Liquidity Status: Funds are locked in the smart contract. The owner cannot withdraw them.",
      report.liquidityLockResult.explicitLockWarning
    )
  }

  @Test
  fun `test liquidity unlocked yields critical explicit english text warning and red alert`() {
    val raw = TokenSecurityRaw(
      tokenName = "Unlocked Token",
      tokenSymbol = "UNLCK",
      isHoneypot = "0",
      isInDex = "1",
      lpLocked = "0",
      buyTax = "0.00",
      sellTax = "0.00"
    )

    val report = SecurityReport.evaluate("0x5555555555555555555555555555555555555555", raw)

    assertFalse(report.liquidityLockResult.isLocked)
    assertEquals(
      "CRITICAL WARNING: Funds are NOT locked! The owner retains full control and can withdraw all money at any time.",
      report.liquidityLockResult.explicitLockWarning
    )
    assertTrue(
      report.warningAlerts.any {
        it.isCritical && it.description.contains("Funds are NOT locked! The owner retains full control and can withdraw all money at any time.")
      }
    )
  }
}
