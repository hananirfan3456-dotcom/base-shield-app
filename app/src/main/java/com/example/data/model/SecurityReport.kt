package com.example.data.model

import java.util.Locale

/**
 * Risk level categorization for Base Shield.
 */
enum class RiskLevel(val label: String) {
  SAFE("LOW RISK / SAFE"),
  MODERATE("MODERATE RISK"),
  HIGH_RISK("HIGH RISK / DANGEROUS")
}

/**
 * Individual high-risk warning alert.
 */
data class WarningAlert(
  val title: String,
  val description: String,
  val isCritical: Boolean = true
)

/**
 * Parameter 1: Honeypot Detection breakdown (Max 40 points).
 */
data class HoneypotParameterResult(
  val score: Int, // 0 to 40
  val maxScore: Int = 40,
  val isHoneypot: Boolean,
  val cannotBuy: Boolean,
  val cannotSellAll: Boolean,
  val honeypotWithSameCreator: Boolean,
  val transferPausable: Boolean,
  val isBlacklisted: Boolean,
  val passed: Boolean,
  val summary: String
)

/**
 * Parameter 2: Liquidity Lock Status breakdown (Max 30 points).
 */
data class LiquidityLockParameterResult(
  val score: Int, // 0 to 30
  val maxScore: Int = 30,
  val totalLockedPercent: Double, // 0.0 to 100.0%
  val lpHoldersCount: Int,
  val isInDex: Boolean,
  val isLocked: Boolean,
  val explicitLockWarning: String,
  val isLockedAcceptable: Boolean,
  val summary: String,
  val topLpHolders: List<LpHolderSummary>
)

data class LpHolderSummary(
  val address: String,
  val percent: Double,
  val isLocked: Boolean,
  val tag: String
)

/**
 * Parameter 3: Excessive Buy/Sell Taxes breakdown (Max 30 points).
 */
data class TaxParameterResult(
  val score: Int, // 0 to 30
  val maxScore: Int = 30,
  val buyTaxPercent: Double,
  val sellTaxPercent: Double,
  val isModifiable: Boolean,
  val isExcessive: Boolean,
  val summary: String
)

/**
 * Full calculated Safety Score and Audit Report for a Base token.
 */
data class SecurityReport(
  val contractAddress: String,
  val tokenName: String,
  val tokenSymbol: String,
  val totalSupply: String,
  val holderCount: String,
  val chainId: String = "8453",

  // Overarching Safety Score out of 100
  val safetyScore: Int,
  val riskLevel: RiskLevel,

  // Three strict parameters
  val honeypotResult: HoneypotParameterResult,
  val liquidityLockResult: LiquidityLockParameterResult,
  val taxResult: TaxParameterResult,

  // Instant Red Warning Alerts
  val warningAlerts: List<WarningAlert>,

  // Additional contract intelligence
  val isOpenSource: Boolean,
  val isMintable: Boolean,
  val isProxy: Boolean,
  val ownerAddress: String?,
  val creatorAddress: String?,
  val scanTimestamp: Long = System.currentTimeMillis()
) {
  companion object {
    /**
     * Evaluates raw GoPlus response data into a strict 3-parameter SecurityReport out of 100.
     */
    fun evaluate(contractAddress: String, raw: TokenSecurityRaw): SecurityReport {
      val alerts = mutableListOf<WarningAlert>()

      // -------------------------------------------------------------
      // PARAMETER 1: Honeypot Detection (Max: 40 points)
      // -------------------------------------------------------------
      val isHoneypot = raw.isHoneypot == "1"
      val cannotBuy = raw.cannotBuy == "1"
      val cannotSellAll = raw.cannotSellAll == "1"
      val creatorHoneypots = raw.honeypotWithSameCreator == "1"
      val transferPausable = raw.transferPausable == "1"
      val isBlacklisted = raw.isBlacklisted == "1"

      var honeypotScore = 40

      if (isHoneypot) {
        honeypotScore = 0
        alerts.add(
          WarningAlert(
            title = "CRITICAL: HONEYPOT DETECTED",
            description = "This token contract restricts selling. Buyers are unable to sell tokens after purchase!",
            isCritical = true
          )
        )
      } else {
        if (cannotBuy) {
          honeypotScore -= 20
          alerts.add(
            WarningAlert(
              title = "CRITICAL: CANNOT BUY",
              description = "Contract prevents standard token purchases.",
              isCritical = true
            )
          )
        }
        if (cannotSellAll) {
          honeypotScore -= 20
          alerts.add(
            WarningAlert(
              title = "CRITICAL: CANNOT SELL ALL TOKENS",
              description = "Contract restricts holders from liquidating their full token balance.",
              isCritical = true
            )
          )
        }
        if (creatorHoneypots) {
          honeypotScore -= 15
          alerts.add(
            WarningAlert(
              title = "WARNING: REPEAT HONEYPOT CREATOR",
              description = "The deployer wallet has previously launched verified honeypot tokens.",
              isCritical = true
            )
          )
        }
        if (transferPausable) {
          honeypotScore -= 10
          alerts.add(
            WarningAlert(
              title = "WARNING: TRANSFER PAUSABLE",
              description = "Contract owner has the permission to pause all token transfers.",
              isCritical = false
            )
          )
        }
        if (isBlacklisted) {
          honeypotScore -= 10
          alerts.add(
            WarningAlert(
              title = "WARNING: BLACKLIST FUNCTION",
              description = "Owner can blacklist specific wallet addresses from trading.",
              isCritical = false
            )
          )
        }
      }
      honeypotScore = honeypotScore.coerceIn(0, 40)

      val honeypotPassed = honeypotScore >= 35 && !isHoneypot && !cannotBuy && !cannotSellAll
      val honeypotSummary = when {
        isHoneypot -> "Honeypot detected! Selling is completely blocked."
        cannotBuy -> "Purchases are blocked by contract code."
        cannotSellAll -> "Partial sell restrictions active."
        creatorHoneypots -> "Deployer has a history of scam honeypots."
        else -> "Passed: No honeypot code or sell restrictions detected."
      }

      val honeypotResult = HoneypotParameterResult(
        score = honeypotScore,
        maxScore = 40,
        isHoneypot = isHoneypot,
        cannotBuy = cannotBuy,
        cannotSellAll = cannotSellAll,
        honeypotWithSameCreator = creatorHoneypots,
        transferPausable = transferPausable,
        isBlacklisted = isBlacklisted,
        passed = honeypotPassed,
        summary = honeypotSummary
      )

      // -------------------------------------------------------------
      // PARAMETER 2: Liquidity Lock Status (Max: 30 points)
      // -------------------------------------------------------------
      val isInDex = raw.isInDex == "1" || (raw.lpHolders != null && raw.lpHolders.isNotEmpty())
      val lpHolders = raw.lpHolders ?: emptyList()
      var totalLockedPercent = 0.0

      val holderSummaries = lpHolders.take(5).map { lp ->
        val percentVal = parsePercentage(lp.percent)
        val isLocked = lp.isLocked == 1 || lp.lpLocked == 1 || isKnownLockerOrBurn(lp.address, lp.tag)
        if (isLocked) {
          totalLockedPercent += percentVal
        }
        LpHolderSummary(
          address = lp.address ?: "Unknown",
          percent = percentVal,
          isLocked = isLocked,
          tag = lp.tag ?: ""
        )
      }

      // If lp_holders list had more items
      if (lpHolders.size > 5) {
        for (i in 5 until lpHolders.size) {
          val lp = lpHolders[i]
          val percentVal = parsePercentage(lp.percent)
          if (lp.isLocked == 1 || lp.lpLocked == 1 || isKnownLockerOrBurn(lp.address, lp.tag)) {
            totalLockedPercent += percentVal
          }
        }
      }

      totalLockedPercent = totalLockedPercent.coerceIn(0.0, 100.0)

      // Strictly read the "lp_locked" data from the API
      val rawLpLocked = raw.lpLocked ?: raw.isLocked
      val isSafeLpLocked = when {
        rawLpLocked == "1" -> true
        rawLpLocked == "0" -> false
        rawLpLocked != null && (rawLpLocked.toDoubleOrNull() ?: 0.0) > 0.0 -> true
        lpHolders.any { (it.isLocked == 1 || it.lpLocked == 1) && (it.percent?.toDoubleOrNull() ?: 0.0) > 0.0 } -> true
        totalLockedPercent >= 50.0 -> true
        else -> false
      }

      val explicitLockWarning = if (isSafeLpLocked) {
        "Liquidity Status: Funds are locked in the smart contract. The owner cannot withdraw them."
      } else {
        "CRITICAL WARNING: Funds are NOT locked! The owner retains full control and can withdraw all money at any time."
      }

      var liquidityScore = when {
        !isInDex -> 10 // Special or native token not on DEX yet
        isSafeLpLocked && totalLockedPercent >= 90.0 -> 30
        isSafeLpLocked && totalLockedPercent >= 50.0 -> 25
        isSafeLpLocked -> 20
        else -> 0 // Unlocked: High risk of rug pull!
      }

      val isLockedAcceptable = isSafeLpLocked && (totalLockedPercent >= 50.0 || !isInDex)

      if (!isSafeLpLocked) {
        alerts.add(
          WarningAlert(
            title = "CRITICAL: FUNDS ARE NOT LOCKED",
            description = "CRITICAL WARNING: Funds are NOT locked! The owner retains full control and can withdraw all money at any time.",
            isCritical = true
          )
        )
      } else if (isInDex && totalLockedPercent < 50.0) {
        alerts.add(
          WarningAlert(
            title = "WARNING: PARTIAL LIQUIDITY LOCK",
            description = "Less than 50% (${String.format(Locale.US, "%.1f", totalLockedPercent)}%) of LP is verified locked.",
            isCritical = false
          )
        )
      }

      val liquiditySummary = explicitLockWarning

      val liquidityResult = LiquidityLockParameterResult(
        score = liquidityScore,
        maxScore = 30,
        totalLockedPercent = totalLockedPercent,
        lpHoldersCount = raw.lpHolderCount?.toIntOrNull() ?: lpHolders.size,
        isInDex = isInDex,
        isLocked = isSafeLpLocked,
        explicitLockWarning = explicitLockWarning,
        isLockedAcceptable = isLockedAcceptable,
        summary = liquiditySummary,
        topLpHolders = holderSummaries
      )

      // -------------------------------------------------------------
      // PARAMETER 3: Excessive Buy / Sell Taxes (Max: 30 points)
      // -------------------------------------------------------------
      val buyTaxVal = parseTax(raw.buyTax)
      val sellTaxVal = parseTax(raw.sellTax)
      val isModifiable = raw.slippageModifiable == "1" || raw.personalSlippageModifiable == "1"

      var taxScore = 30
      val maxTax = maxOf(buyTaxVal, sellTaxVal)

      when {
        maxTax > 50.0 -> {
          taxScore = 0
          alerts.add(
            WarningAlert(
              title = "CRITICAL: EXTREME TAX (>50%)",
              description = "Buy tax: ${String.format(Locale.US, "%.1f", buyTaxVal)}%, Sell tax: ${String.format(Locale.US, "%.1f", sellTaxVal)}%. Extreme extortionate trading tax!",
              isCritical = true
            )
          )
        }
        maxTax > 15.0 -> {
          taxScore = 5
          alerts.add(
            WarningAlert(
              title = "CRITICAL: EXCESSIVE TRADING TAX (>15%)",
              description = "Buy tax: ${String.format(Locale.US, "%.1f", buyTaxVal)}%, Sell tax: ${String.format(Locale.US, "%.1f", sellTaxVal)}%. Significantly high loss on every trade.",
              isCritical = true
            )
          )
        }
        maxTax > 5.0 -> {
          taxScore = 18
          alerts.add(
            WarningAlert(
              title = "WARNING: MODERATE TAX (>5%)",
              description = "Buy tax: ${String.format(Locale.US, "%.1f", buyTaxVal)}%, Sell tax: ${String.format(Locale.US, "%.1f", sellTaxVal)}%.",
              isCritical = false
            )
          )
        }
        else -> {
          taxScore = 30
        }
      }

      if (isModifiable) {
        taxScore = (taxScore - 10).coerceAtLeast(0)
        alerts.add(
          WarningAlert(
            title = "WARNING: MODIFIABLE TAXES / SLIPPAGE",
            description = "Contract owner possesses privileges to modify taxes arbitrarily after launch.",
            isCritical = true
          )
        )
      }

      val isExcessive = maxTax > 15.0
      val taxSummary = when {
        maxTax > 50.0 -> "Predatory taxes detected: Buy ${String.format(Locale.US, "%.1f", buyTaxVal)}% / Sell ${String.format(Locale.US, "%.1f", sellTaxVal)}%."
        maxTax > 15.0 -> "Excessive tax threshold breached: Buy ${String.format(Locale.US, "%.1f", buyTaxVal)}% / Sell ${String.format(Locale.US, "%.1f", sellTaxVal)}%."
        maxTax > 5.0 -> "Moderate taxes: Buy ${String.format(Locale.US, "%.1f", buyTaxVal)}% / Sell ${String.format(Locale.US, "%.1f", sellTaxVal)}%."
        else -> "Low / Zero tax structure: Buy ${String.format(Locale.US, "%.1f", buyTaxVal)}% / Sell ${String.format(Locale.US, "%.1f", sellTaxVal)}%."
      }

      val taxResult = TaxParameterResult(
        score = taxScore,
        maxScore = 30,
        buyTaxPercent = buyTaxVal,
        sellTaxPercent = sellTaxVal,
        isModifiable = isModifiable,
        isExcessive = isExcessive,
        summary = taxSummary
      )

      // -------------------------------------------------------------
      // Total Safety Score (0 to 100) & Risk Level
      // -------------------------------------------------------------
      var totalScore = (honeypotScore + liquidityScore + taxScore).coerceIn(0, 100)

      // Additional contract flags
      val isOpenSource = raw.isOpenSource == "1"
      val isMintable = raw.isMintable == "1"
      val isProxy = raw.isProxy == "1"

      if (!isOpenSource) {
        totalScore = (totalScore - 10).coerceAtLeast(0)
        alerts.add(
          WarningAlert(
            title = "WARNING: UNVERIFIED CONTRACT CODE",
            description = "Source code is not open-source or verified on BaseScan.",
            isCritical = false
          )
        )
      }

      if (isMintable) {
        totalScore = (totalScore - 10).coerceAtLeast(0)
        alerts.add(
          WarningAlert(
            title = "WARNING: MINTABLE TOKEN",
            description = "Owner has ability to mint additional tokens, causing rapid supply inflation.",
            isCritical = false
          )
        )
      }

      val riskLevel = when {
        totalScore >= 80 && alerts.none { it.isCritical } -> RiskLevel.SAFE
        totalScore >= 50 && alerts.none { it.isCritical && it.title.contains("HONEYPOT") } -> RiskLevel.MODERATE
        else -> RiskLevel.HIGH_RISK
      }

      return SecurityReport(
        contractAddress = contractAddress,
        tokenName = raw.tokenName ?: "Unknown Token",
        tokenSymbol = raw.tokenSymbol ?: "UNKNOWN",
        totalSupply = raw.totalSupply ?: "N/A",
        holderCount = raw.holderCount ?: "0",
        safetyScore = totalScore,
        riskLevel = riskLevel,
        honeypotResult = honeypotResult,
        liquidityLockResult = liquidityResult,
        taxResult = taxResult,
        warningAlerts = alerts,
        isOpenSource = isOpenSource,
        isMintable = isMintable,
        isProxy = isProxy,
        ownerAddress = raw.ownerAddress,
        creatorAddress = raw.creatorAddress
      )
    }

    private fun parsePercentage(raw: String?): Double {
      if (raw.isNullOrBlank()) return 0.0
      val d = raw.toDoubleOrNull() ?: return 0.0
      return if (d <= 1.0) d * 100.0 else d
    }

    private fun parseTax(raw: String?): Double {
      if (raw.isNullOrBlank()) return 0.0
      val d = raw.toDoubleOrNull() ?: return 0.0
      return if (d <= 1.0) d * 100.0 else d
    }

    private fun isKnownLockerOrBurn(address: String?, tag: String?): Boolean {
      if (address == null) return false
      val lower = address.lowercase()
      if (lower.startsWith("0x0000000000000000000000000000000000000000") ||
          lower.startsWith("0x000000000000000000000000000000000000dead")) {
        return true
      }
      val tagLower = (tag ?: "").lowercase()
      return tagLower.contains("lock") || tagLower.contains("burn") || tagLower.contains("unicrypt") || tagLower.contains("pink")
    }
  }
}
