package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BaseToken
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.BaseBlue
import com.example.ui.theme.BaseBlueLight
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TokenItemCard(
  token: BaseToken,
  onScanClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isPositiveChange = token.priceChange24h >= 0.0

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("token_card_${token.symbol}")
      .clickable { onScanClick(token.address) },
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Logo/Avatar, Name, Symbol, and Price
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          // Token Avatar
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(BaseBlue.copy(alpha = 0.2f))
              .border(1.dp, BaseBlueLight.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (!token.imageUrl.isNullOrBlank()) {
              AsyncImage(
                model = token.imageUrl,
                contentDescription = token.name,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape),
                contentScale = ContentScale.Crop
              )
            } else {
              Text(
                text = token.symbol.take(3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan
              )
            }
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = token.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
              )

              if (token.isNewlyLaunched) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberCyan.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "NEW",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyberCyan
                  )
                }
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = token.symbol,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
              )
              Text(text = "•", fontSize = 10.sp, color = TextMuted)
              Text(
                text = "BASE 8453",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = BaseBlueLight
              )
            }
          }
        }

        // Price and 24h Change
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = token.formattedPrice,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(2.dp))

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isPositiveChange) SafeGreenBg else AlertRedBg)
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Icon(
              imageVector = if (isPositiveChange) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
              contentDescription = null,
              tint = if (isPositiveChange) SafeGreen else AlertRed,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = token.formattedChange24h,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = if (isPositiveChange) SafeGreen else AlertRed
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Middle Row: Contract Address & Quick Copy
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(DarkSurfaceVariant)
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "CA:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
          Text(
            text = token.truncatedAddress,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary
          )
        }

        IconButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Base Contract Address", token.address))
            Toast.makeText(context, "Contract address copied!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Contract",
            tint = CyberCyan,
            modifier = Modifier.size(13.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Volume + Instant Scan Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "24h Vol:",
            fontSize = 11.sp,
            color = TextMuted
          )
          Text(
            text = token.formattedVolume,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Text(text = "•", fontSize = 10.sp, color = TextMuted)
          Text(
            text = token.dexName,
            fontSize = 10.sp,
            color = TextSecondary
          )
        }

        Button(
          onClick = { onScanClick(token.address) },
          colors = ButtonDefaults.buttonColors(
            containerColor = BaseBlue,
            contentColor = TextPrimary
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(30.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = CyberCyan
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Shield Scan",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
