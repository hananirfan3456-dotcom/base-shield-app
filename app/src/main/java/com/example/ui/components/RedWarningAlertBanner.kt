package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WarningAlert
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.AlertRedBorder
import com.example.ui.theme.AlertRedBright
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.SafeGreenBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg
import com.example.ui.theme.WarningAmberBorder

@Composable
fun RedWarningAlertBanner(
  alerts: List<WarningAlert>,
  modifier: Modifier = Modifier
) {
  val criticalCount = alerts.count { it.isCritical }
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_alert")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.65f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("red_warning_alert_banner")
  ) {
    if (alerts.isNotEmpty()) {
      // High-Risk Alert Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(AlertRedBg)
          .border(
            width = if (criticalCount > 0) 1.5.dp else 1.dp,
            color = if (criticalCount > 0) AlertRed.copy(alpha = pulseAlpha) else AlertRedBorder,
            shape = RoundedCornerShape(16.dp)
          )
          .padding(16.dp)
      ) {
        Column {
          // Header Row
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
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(AlertRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (criticalCount > 0) Icons.Default.Error else Icons.Default.Warning,
                  contentDescription = "Security Alert",
                  tint = AlertRedBright,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = if (criticalCount > 0) "HIGH RISK WARNING ALERTS" else "SECURITY WARNINGS",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp,
                  color = AlertRedBright
                )
                Text(
                  text = "$criticalCount Critical, ${alerts.size - criticalCount} Advisory flags",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            // Flashing Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AlertRed)
                .alpha(pulseAlpha)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "ACTION REQUIRED",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Individual Alert Items
          alerts.forEachIndexed { index, alert ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (alert.isCritical) AlertRed.copy(alpha = 0.15f) else WarningAmberBg.copy(alpha = 0.4f))
                .border(
                  width = 1.dp,
                  color = if (alert.isCritical) AlertRedBorder else WarningAmberBorder,
                  shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = if (alert.isCritical) Icons.Default.Error else Icons.Default.Warning,
                contentDescription = null,
                tint = if (alert.isCritical) AlertRedBright else WarningAmber,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 2.dp)
              )

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = alert.title,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (alert.isCritical) AlertRedBright else WarningAmber
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = alert.description,
                  fontSize = 11.sp,
                  color = TextPrimary.copy(alpha = 0.9f),
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      }
    } else {
      // Safe Green Badge
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(SafeGreenBg)
          .border(1.dp, SafeGreenBorder, RoundedCornerShape(14.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Protected",
          tint = SafeGreen,
          modifier = Modifier.size(22.dp)
        )
        Column {
          Text(
            text = "SHIELD VERIFIED: NO HIGH-RISK FLAGS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SafeGreen
          )
          Text(
            text = "Honeypot, Liquidity Lock, and Tax limits meet Base security standards.",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }
    }
  }
}
