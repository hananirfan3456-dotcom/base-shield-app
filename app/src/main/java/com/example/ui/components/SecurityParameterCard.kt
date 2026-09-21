package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HoneypotParameterResult
import com.example.data.model.LiquidityLockParameterResult
import com.example.data.model.TaxParameterResult
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.AlertRedBorder
import com.example.ui.theme.BaseBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.SafeGreenBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg
import com.example.ui.theme.WarningAmberBorder
import java.util.Locale

// -------------------------------------------------------------
// PARAMETER 1: Honeypot Detection Card (Max 40 Pts)
// -------------------------------------------------------------
@Composable
fun HoneypotParameterCard(
  result: HoneypotParameterResult,
  modifier: Modifier = Modifier
) {
  val isPass = result.passed
  val (statusColor, statusBg, statusBorder) = if (isPass) {
    Triple(SafeGreen, SafeGreenBg, SafeGreenBorder)
  } else {
    Triple(AlertRed, AlertRedBg, AlertRedBorder)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("honeypot_parameter_card"),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (!isPass) AlertRedBorder else DarkBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(statusBg)
              .border(1.dp, statusBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = "Honeypot",
              tint = statusColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = "1. HONEYPOT DETECTION",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = TextPrimary
            )
            Text(
              text = "Anti-fraud trading simulation",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Score Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(statusBg)
            .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${result.score}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = statusColor
          )
          Text(
            text = "/${result.maxScore} PTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Status summary box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(DarkSurfaceVariant)
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Text(
          text = result.summary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = if (isPass) TextPrimary else AlertRed
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Checklist Grid
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ChecklistItem(
          label = "Honeypot Trap Code",
          isViolation = result.isHoneypot,
          violationText = "DETECTED - SELL BLOCKED"
        )
        ChecklistItem(
          label = "Buy Restrictions",
          isViolation = result.cannotBuy,
          violationText = "CANNOT BUY"
        )
        ChecklistItem(
          label = "Sell All Restriction",
          isViolation = result.cannotSellAll,
          violationText = "CANNOT SELL 100%"
        )
        ChecklistItem(
          label = "Creator Honeypot History",
          isViolation = result.honeypotWithSameCreator,
          violationText = "PREVIOUS SCAM CREATOR"
        )
        ChecklistItem(
          label = "Trading Pausable",
          isViolation = result.transferPausable,
          violationText = "PAUSABLE BY OWNER"
        )
        ChecklistItem(
          label = "Blacklist Mechanism",
          isViolation = result.isBlacklisted,
          violationText = "BLACKLIST PRESENT"
        )
      }
    }
  }
}

// -------------------------------------------------------------
// PARAMETER 2: Liquidity Lock Status Card (Max 30 Pts)
// -------------------------------------------------------------
@Composable
fun LiquidityLockParameterCard(
  result: LiquidityLockParameterResult,
  modifier: Modifier = Modifier
) {
  val isLocked = result.isLocked
  val (statusColor, statusBg, statusBorder) = if (isLocked) {
    Triple(SafeGreen, SafeGreenBg, SafeGreenBorder)
  } else {
    Triple(AlertRed, AlertRedBg, AlertRedBorder)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("liquidity_lock_parameter_card"),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (!isLocked) AlertRedBorder else DarkBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(statusBg)
              .border(1.dp, statusBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
              contentDescription = "Liquidity Lock",
              tint = statusColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = "2. LIQUIDITY LOCK STATUS",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = TextPrimary
            )
            Text(
              text = "GoPlus \"lp_locked\" audit analysis",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Score Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(statusBg)
            .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${result.score}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = statusColor
          )
          Text(
            text = "/${result.maxScore} PTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Explicit English Text Warning Banner based strictly on "lp_locked" data from API
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isLocked) SafeGreenBg else AlertRedBg)
          .border(
            width = 1.5.dp,
            color = if (isLocked) SafeGreenBorder else AlertRed,
            shape = RoundedCornerShape(12.dp)
          )
          .padding(14.dp)
          .testTag("explicit_liquidity_lock_warning")
      ) {
        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = null,
            tint = if (isLocked) SafeGreen else AlertRed,
            modifier = Modifier
              .size(22.dp)
              .padding(top = 2.dp)
          )
          Column {
            Text(
              text = if (isLocked) "LIQUIDITY STATUS (LP LOCKED)" else "CRITICAL LIQUIDITY WARNING (UNLOCKED)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.6.sp,
              color = if (isLocked) SafeGreen else AlertRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = result.explicitLockWarning,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 17.sp,
              color = TextPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Locked Percentage Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(DarkSurfaceVariant)
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total LP Locked / Burned:",
            fontSize = 12.sp,
            color = TextSecondary
          )
          Text(
            text = String.format(Locale.US, "%.1f%%", result.totalLockedPercent),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = statusColor
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
          progress = { (result.totalLockedPercent / 100f).toFloat().coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
          color = statusColor,
          trackColor = Color(0xFF0D1524)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = result.summary,
        fontSize = 11.sp,
        color = TextSecondary,
        lineHeight = 15.sp
      )

      if (result.topLpHolders.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Top LP Holders (${result.lpHoldersCount} total holders):",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        result.topLpHolders.take(3).forEach { holder ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(
                imageVector = if (holder.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (holder.isLocked) SafeGreen else AlertRed,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = if (holder.address.length >= 10) "${holder.address.take(6)}...${holder.address.takeLast(4)}" else holder.address,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
              )
              if (holder.tag.isNotBlank()) {
                Text(
                  text = "(${holder.tag})",
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }

            Text(
              text = String.format(Locale.US, "%.1f%%", holder.percent),
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = if (holder.isLocked) SafeGreen else TextPrimary
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PARAMETER 3: Excessive Buy / Sell Taxes Card (Max 30 Pts)
// -------------------------------------------------------------
@Composable
fun TaxParameterCard(
  result: TaxParameterResult,
  modifier: Modifier = Modifier
) {
  val isHighRisk = result.isExcessive || result.isModifiable
  val (statusColor, statusBg, statusBorder) = when {
    result.buyTaxPercent <= 5.0 && result.sellTaxPercent <= 5.0 && !result.isModifiable ->
      Triple(SafeGreen, SafeGreenBg, SafeGreenBorder)
    result.buyTaxPercent <= 15.0 && result.sellTaxPercent <= 15.0 ->
      Triple(WarningAmber, WarningAmberBg, WarningAmberBorder)
    else ->
      Triple(AlertRed, AlertRedBg, AlertRedBorder)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("tax_parameter_card"),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isHighRisk) AlertRedBorder else DarkBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(statusBg)
              .border(1.dp, statusBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CurrencyExchange,
              contentDescription = "Taxes",
              tint = statusColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = "3. EXCESSIVE BUY / SELL TAXES",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = TextPrimary
            )
            Text(
              text = "Contract fees & slippage limits",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Score Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(statusBg)
            .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${result.score}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = statusColor
          )
          Text(
            text = "/${result.maxScore} PTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Tax Metrics Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Buy Tax Box
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, if (result.buyTaxPercent > 15.0) AlertRedBorder else DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "BUY TAX",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = String.format(Locale.US, "%.1f%%", result.buyTaxPercent),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = when {
              result.buyTaxPercent > 15.0 -> AlertRed
              result.buyTaxPercent > 5.0 -> WarningAmber
              else -> SafeGreen
            }
          )
          Text(
            text = if (result.buyTaxPercent <= 5.0) "Low / Normal" else if (result.buyTaxPercent <= 15.0) "Moderate" else "HIGH RISK",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
        }

        // Sell Tax Box
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, if (result.sellTaxPercent > 15.0) AlertRedBorder else DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "SELL TAX",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = String.format(Locale.US, "%.1f%%", result.sellTaxPercent),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = when {
              result.sellTaxPercent > 15.0 -> AlertRed
              result.sellTaxPercent > 5.0 -> WarningAmber
              else -> SafeGreen
            }
          )
          Text(
            text = if (result.sellTaxPercent <= 5.0) "Low / Normal" else if (result.sellTaxPercent <= 15.0) "Moderate" else "HIGH RISK",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Modifiable taxes check
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(if (result.isModifiable) AlertRedBg else DarkSurfaceVariant)
          .border(1.dp, if (result.isModifiable) AlertRedBorder else DarkBorder, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Modifiable Taxes / Slippage:",
          fontSize = 11.sp,
          color = TextSecondary
        )
        Text(
          text = if (result.isModifiable) "YES (OWNER CAN CHANGE)" else "NO (LOCKED TAX)",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (result.isModifiable) AlertRed else SafeGreen
        )
      }
    }
  }
}

// -------------------------------------------------------------
// Reusable Checklist Item
// -------------------------------------------------------------
@Composable
private fun ChecklistItem(
  label: String,
  isViolation: Boolean,
  violationText: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = if (isViolation) Icons.Default.Cancel else Icons.Default.CheckCircle,
        contentDescription = null,
        tint = if (isViolation) AlertRed else SafeGreen,
        modifier = Modifier.size(15.dp)
      )
      Text(
        text = label,
        fontSize = 11.sp,
        color = if (isViolation) TextPrimary else TextSecondary
      )
    }

    Text(
      text = if (isViolation) violationText else "PASSED",
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = if (isViolation) AlertRed else SafeGreen
    )
  }
}
