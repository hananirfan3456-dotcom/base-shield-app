package com.example.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SecurityReport
import com.example.data.repository.TokenFilterCategory
import com.example.ui.components.HoneypotParameterCard
import com.example.ui.components.LiquidityLockParameterCard
import com.example.ui.components.RedWarningAlertBanner
import com.example.ui.components.SafetyScoreGauge
import com.example.ui.components.TaxParameterCard
import com.example.ui.components.TokenItemCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.BaseBlue
import com.example.ui.theme.BaseBlueLight
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.SafeGreenBorder
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppDashboardTab
import com.example.ui.viewmodel.BaseShieldViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseShieldDashboardScreen(
  viewModel: BaseShieldViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val keyboardController = LocalSoftwareKeyboardController.current

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground),
    containerColor = DarkBackground
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // -------------------------------------------------------------
      // TOP BRAND HEADER
      // -------------------------------------------------------------
      TopBrandHeader(refreshCountdown = uiState.refreshCountdown)

      // -------------------------------------------------------------
      // NAVIGATION TAB ROW
      // -------------------------------------------------------------
      DashboardNavigationTabs(
        currentTab = uiState.currentTab,
        onTabSelected = { viewModel.switchTab(it) }
      )

      // -------------------------------------------------------------
      // TAB CONTENT
      // -------------------------------------------------------------
      when (uiState.currentTab) {
        AppDashboardTab.DEX_FEED -> {
          DexFeedTabContent(
            uiState = uiState,
            onCategorySelect = { viewModel.selectCategory(it) },
            onFilterQueryChange = { viewModel.setTokenFilterQuery(it) },
            onRefresh = { viewModel.loadTokens(uiState.selectedCategory, forceRefresh = true) },
            onScanClick = { viewModel.scanToken(it) }
          )
        }

        AppDashboardTab.SCANNER -> {
          ScannerTabContent(
            uiState = uiState,
            presets = viewModel.presets,
            onAddressChange = { viewModel.setScanAddressInput(it) },
            onScanSubmit = {
              keyboardController?.hide()
              viewModel.scanToken(uiState.scanAddressInput)
            },
            onPresetSelect = { preset ->
              keyboardController?.hide()
              viewModel.selectPreset(preset)
            }
          )
        }

        AppDashboardTab.HISTORY -> {
          ScanHistoryTabContent(
            history = uiState.recentScans,
            onReScan = { viewModel.scanToken(it) }
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// TOP BRAND HEADER
// -------------------------------------------------------------
@Composable
private fun TopBrandHeader(refreshCountdown: Int) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(DarkSurface)
      .border(1.dp, DarkBorder)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(BaseBlue)
          .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "Base Shield Logo",
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }

      Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "BASE SHIELD",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            color = TextPrimary
          )
          Text(
            text = "SCANNER",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan
          )
        }
        Text(
          text = "GoPlus Real-Time Security Engine",
          fontSize = 10.sp,
          color = TextSecondary
        )
      }
    }

    // Network & Sync Badge
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(DarkSurfaceVariant)
        .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
        .padding(horizontal = 10.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(SafeGreen)
      )
      Text(
        text = "CHAIN 8453",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = CyberCyan
      )
    }
  }
}

// -------------------------------------------------------------
// DASHBOARD NAVIGATION TABS
// -------------------------------------------------------------
@Composable
private fun DashboardNavigationTabs(
  currentTab: AppDashboardTab,
  onTabSelected: (AppDashboardTab) -> Unit
) {
  val tabs = AppDashboardTab.entries

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(DarkSurfaceVariant)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    tabs.forEach { tab ->
      val isSelected = currentTab == tab
      val testTag = when (tab) {
        AppDashboardTab.DEX_FEED -> "tab_dex_feed"
        AppDashboardTab.SCANNER -> "tab_scanner"
        AppDashboardTab.HISTORY -> "tab_history"
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(10.dp))
          .background(if (isSelected) BaseBlue else Color.Transparent)
          .border(
            width = 1.dp,
            color = if (isSelected) CyberCyan.copy(alpha = 0.6f) else Color.Transparent,
            shape = RoundedCornerShape(10.dp)
          )
          .clickable { onTabSelected(tab) }
          .padding(vertical = 8.dp)
          .testTag(testTag),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = when (tab) {
              AppDashboardTab.DEX_FEED -> Icons.Default.TrendingUp
              AppDashboardTab.SCANNER -> Icons.Default.Shield
              AppDashboardTab.HISTORY -> Icons.Default.History
            },
            contentDescription = null,
            tint = if (isSelected) Color.White else TextSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = tab.title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// TAB 1: DEX LIVE FEED
// -------------------------------------------------------------
@Composable
private fun DexFeedTabContent(
  uiState: com.example.ui.viewmodel.BaseShieldUiState,
  onCategorySelect: (TokenFilterCategory) -> Unit,
  onFilterQueryChange: (String) -> Unit,
  onRefresh: () -> Unit,
  onScanClick: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Category Selector Chips & Refresh Timer
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TokenFilterCategory.entries.forEach { cat ->
          val isSelected = uiState.selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
              .border(
                1.dp,
                if (isSelected) CyberCyan else DarkBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { onCategorySelect(cat) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = cat.displayName,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) CyberCyan else TextSecondary
            )
          }
        }
      }

      // Manual Refresh Icon + Countdown
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "${uiState.refreshCountdown}s",
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          color = TextMuted
        )
        IconButton(
          onClick = onRefresh,
          modifier = Modifier
            .size(32.dp)
            .testTag("refresh_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh DEX List",
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Token Filter Search Input
    OutlinedTextField(
      value = uiState.tokenFilterQuery,
      onValueChange = onFilterQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("token_filter_input"),
      placeholder = { Text("Filter Base tokens by name or address...", fontSize = 12.sp, color = TextMuted) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(18.dp))
      },
      trailingIcon = {
        if (uiState.tokenFilterQuery.isNotEmpty()) {
          IconButton(onClick = { onFilterQueryChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
          }
        }
      },
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedBorderColor = CyberCyan,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true,
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Results Header Count
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "LIVE BASE NETWORK DEX FEED (CHAIN 8453)",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = TextMuted
      )
      Text(
        text = "${uiState.filteredTokens.size} Pairs",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        color = TextSecondary
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Content: Loading, Error, or Token List
    Box(modifier = Modifier.weight(1f)) {
      if (uiState.isTokensLoading && uiState.tokens.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Querying Base DEX Pools...", fontSize = 12.sp, color = TextSecondary)
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 16.dp)
        ) {
          items(uiState.filteredTokens, key = { it.address + it.symbol }) { token ->
            TokenItemCard(
              token = token,
              onScanClick = onScanClick
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// TAB 2: LIVE SHIELD SCANNER
// -------------------------------------------------------------
@Composable
private fun ScannerTabContent(
  uiState: com.example.ui.viewmodel.BaseShieldUiState,
  presets: List<com.example.data.repository.QuickScanPreset>,
  onAddressChange: (String) -> Unit,
  onScanSubmit: () -> Unit,
  onPresetSelect: (com.example.data.repository.QuickScanPreset) -> Unit
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
  ) {
    // Search Bar Box
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("scanner_search_box"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
            Text(
              text = "LIVE CONTRACT ADDRESS SCANNER",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.6.sp,
              color = TextSecondary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Contract Input Field
          OutlinedTextField(
            value = uiState.scanAddressInput,
            onValueChange = onAddressChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("scan_input_field"),
            placeholder = { Text("Enter Base contract address (0x...)", fontSize = 12.sp, color = TextMuted) },
            trailingIcon = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (uiState.scanAddressInput.isNotEmpty()) {
                  IconButton(onClick = { onAddressChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                  }
                }
                // Paste from clipboard button
                IconButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                      val text = clip.getItemAt(0).text?.toString() ?: ""
                      if (text.isNotBlank()) {
                        onAddressChange(text.trim())
                      }
                    }
                  },
                  modifier = Modifier.testTag("paste_button")
                ) {
                  Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = CyberCyan, modifier = Modifier.size(16.dp))
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = DarkSurfaceVariant,
              unfocusedContainerColor = DarkSurfaceVariant,
              focusedBorderColor = CyberCyan,
              unfocusedBorderColor = DarkBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onScanSubmit() })
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Scan Button
          Button(
            onClick = onScanSubmit,
            enabled = !uiState.isScanning,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("scan_submit_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = BaseBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (uiState.isScanning) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Scanning GoPlus API...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            } else {
              Icon(Icons.Default.Shield, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "SCAN CONTRACT SAFETY", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Presets Row
          Text(
            text = "QUICK TEST PRESETS:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            presets.forEach { preset ->
              val isAlertDemo = preset.symbol == "ALERT-DEMO"
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isAlertDemo) AlertRedBg else DarkSurfaceElevated)
                  .border(
                    1.dp,
                    if (isAlertDemo) AlertRed else DarkBorder,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { onPresetSelect(preset) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("preset_${preset.symbol}")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = preset.symbol,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAlertDemo) AlertRed else CyberCyan
                  )
                  Text(
                    text = "• ${preset.label}",
                    fontSize = 10.sp,
                    color = TextSecondary
                  )
                }
              }
            }
          }
        }
      }
    }

    // Error Box (if any)
    if (uiState.scanError != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = AlertRedBg),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.Clear, contentDescription = "Error", tint = AlertRed)
            Text(text = uiState.scanError ?: "", color = TextPrimary, fontSize = 12.sp)
          }
        }
      }
    }

    // Active Security Report
    if (uiState.activeReport != null) {
      val report = uiState.activeReport!!

      // Token Header Card
      item {
        TokenScanHeaderCard(report = report)
      }

      // Safety Score Gauge Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            SafetyScoreGauge(
              score = report.safetyScore,
              riskLevel = report.riskLevel
            )
          }
        }
      }

      // Instant Red Warning Alerts Banner
      item {
        RedWarningAlertBanner(alerts = report.warningAlerts)
      }

      // Three Strict Parameters Breakdown
      item {
        Text(
          text = "CORE SECURITY AUDIT PARAMETERS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp,
          color = TextMuted
        )
      }

      // Parameter 1: Honeypot Detection (40 Pts)
      item {
        HoneypotParameterCard(result = report.honeypotResult)
      }

      // Parameter 2: Liquidity Lock Status (30 Pts)
      item {
        LiquidityLockParameterCard(result = report.liquidityLockResult)
      }

      // Parameter 3: Excessive Buy/Sell Taxes (30 Pts)
      item {
        TaxParameterCard(result = report.taxResult)
      }

      // Additional Contract Intelligence
      item {
        ContractIntelligenceCard(report = report)
      }
    }
  }
}

// -------------------------------------------------------------
// TOKEN SCAN HEADER CARD
// -------------------------------------------------------------
@Composable
private fun TokenScanHeaderCard(report: SecurityReport) {
  val context = LocalContext.current

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = report.tokenName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = TextPrimary
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(BaseBlue.copy(alpha = 0.2f))
                .border(1.dp, BaseBlueLight.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = report.tokenSymbol,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan
              )
            }
          }

          Text(
            text = "Chain ID: 8453 (Base Network)",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        // External Link to BaseScan
        IconButton(
          onClick = {
            val intent = Intent(
              Intent.ACTION_VIEW,
              Uri.parse("https://basescan.org/token/${report.contractAddress}")
            )
            try {
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Icon(Icons.Default.OpenInNew, contentDescription = "View on BaseScan", tint = CyberCyan)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Contract Address Box with Copy
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(DarkSurfaceVariant)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = report.contractAddress,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        IconButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Base Contract Address", report.contractAddress))
            Toast.makeText(context, "Address copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan, modifier = Modifier.size(14.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------
// CONTRACT INTELLIGENCE CARD
// -------------------------------------------------------------
@Composable
private fun ContractIntelligenceCard(report: SecurityReport) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "CONTRACT ARCHITECTURE & AUDIT METRICS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AuditMetricPill(
          label = "Open Source",
          value = if (report.isOpenSource) "Verified" else "Unverified",
          isGood = report.isOpenSource,
          modifier = Modifier.weight(1f)
        )
        AuditMetricPill(
          label = "Mintable",
          value = if (report.isMintable) "YES (Risk)" else "NO (Fixed)",
          isGood = !report.isMintable,
          modifier = Modifier.weight(1f)
        )
        AuditMetricPill(
          label = "Proxy Pattern",
          value = if (report.isProxy) "YES (Upgradable)" else "NO",
          isGood = !report.isProxy,
          modifier = Modifier.weight(1f)
        )
      }

      if (!report.ownerAddress.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Owner: ${report.ownerAddress}",
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
private fun AuditMetricPill(
  label: String,
  value: String,
  isGood: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(DarkSurfaceVariant)
      .border(1.dp, if (isGood) SafeGreenBorder else AlertRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
      .padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(text = label, fontSize = 9.sp, color = TextMuted)
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isGood) SafeGreen else AlertRed
    )
  }
}

// -------------------------------------------------------------
// TAB 3: SCAN HISTORY
// -------------------------------------------------------------
@Composable
private fun ScanHistoryTabContent(
  history: List<SecurityReport>,
  onReScan: (String) -> Unit
) {
  if (history.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "No Previous Scans", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Text(text = "Tokens scanned using the search bar appear here.", fontSize = 12.sp, color = TextMuted)
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp)
    ) {
      items(history) { report ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onReScan(report.contractAddress) },
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${report.tokenName} (${report.tokenSymbol})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = if (report.contractAddress.length >= 12)
                  "${report.contractAddress.take(8)}...${report.contractAddress.takeLast(6)}"
                else report.contractAddress,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
              )
            }

            // Score Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  when (report.riskLevel) {
                    com.example.data.model.RiskLevel.SAFE -> SafeGreenBg
                    com.example.data.model.RiskLevel.MODERATE -> com.example.ui.theme.WarningAmberBg
                    com.example.data.model.RiskLevel.HIGH_RISK -> AlertRedBg
                  }
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "${report.safetyScore}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = when (report.riskLevel) {
                  com.example.data.model.RiskLevel.SAFE -> SafeGreen
                  com.example.data.model.RiskLevel.MODERATE -> WarningAmber
                  com.example.data.model.RiskLevel.HIGH_RISK -> AlertRed
                }
              )
              Text(
                text = "/100",
                fontSize = 10.sp,
                color = TextMuted
              )
            }
          }
        }
      }
    }
  }
}
