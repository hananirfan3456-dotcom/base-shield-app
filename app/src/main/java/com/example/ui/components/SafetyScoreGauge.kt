package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.AlertRedBorder
import com.example.ui.theme.BaseBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.SafeGreenBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg
import com.example.ui.theme.WarningAmberBorder

@Composable
fun SafetyScoreGauge(
  score: Int,
  riskLevel: RiskLevel,
  modifier: Modifier = Modifier,
  size: Dp = 190.dp
) {
  val animatedProgress by animateFloatAsState(
    targetValue = (score / 100f).coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
    label = "score_gauge_anim"
  )

  val (statusColor, badgeBg, badgeBorder, statusIcon) = when (riskLevel) {
    RiskLevel.SAFE -> Quadruple(SafeGreen, SafeGreenBg, SafeGreenBorder, Icons.Default.GppGood)
    RiskLevel.MODERATE -> Quadruple(WarningAmber, WarningAmberBg, WarningAmberBorder, Icons.Default.GppMaybe)
    RiskLevel.HIGH_RISK -> Quadruple(AlertRed, AlertRedBg, AlertRedBorder, Icons.Default.GppBad)
  }

  Column(
    modifier = modifier.testTag("safety_score_gauge"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier.size(size),
      contentAlignment = Alignment.Center
    ) {
      // Glow circle background
      Box(
        modifier = Modifier
          .size(size * 0.82f)
          .clip(CircleShape)
          .background(DarkBackground)
          .border(1.dp, DarkBorder, CircleShape)
      )

      // Gauge arc drawing
      Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = 14.dp.toPx()
        val diameter = size.toPx() - strokeWidth
        val topLeft = strokeWidth / 2f

        // Background track (260 degree arc)
        drawArc(
          color = Color(0xFF182236),
          startAngle = 140f,
          sweepAngle = 260f,
          useCenter = false,
          topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
          size = androidx.compose.ui.geometry.Size(diameter, diameter),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Value arc with gradient
        val arcGradient = Brush.sweepGradient(
          colors = when (riskLevel) {
            RiskLevel.SAFE -> listOf(BaseBlue, CyberCyan, SafeGreen)
            RiskLevel.MODERATE -> listOf(BaseBlue, WarningAmber)
            RiskLevel.HIGH_RISK -> listOf(AlertRed, Color(0xFFFF6B81), AlertRed)
          }
        )

        drawArc(
          brush = arcGradient,
          startAngle = 140f,
          sweepAngle = 260f * animatedProgress,
          useCenter = false,
          topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
          size = androidx.compose.ui.geometry.Size(diameter, diameter),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      // Center Content
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "$score",
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = statusColor
          )
          Text(
            text = "/100",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }

        Text(
          text = "BASE SHIELD SCORE",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          color = TextMuted
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Risk Level Status Pill
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(badgeBg)
        .border(1.dp, badgeBorder, RoundedCornerShape(20.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = statusIcon,
        contentDescription = null,
        tint = statusColor,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = riskLevel.label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = statusColor
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
