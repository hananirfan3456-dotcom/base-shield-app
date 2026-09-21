package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BaseToken
import com.example.data.model.SecurityReport
import com.example.data.repository.BaseShieldRepository
import com.example.data.repository.QuickScanPreset
import com.example.data.repository.TokenFilterCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppDashboardTab(val title: String) {
  DEX_FEED("Live DEX Tokens"),
  SCANNER("Shield Scanner"),
  HISTORY("Scan History")
}

data class BaseShieldUiState(
  val currentTab: AppDashboardTab = AppDashboardTab.DEX_FEED,
  val selectedCategory: TokenFilterCategory = TokenFilterCategory.NEWLY_LAUNCHED,

  // Token list state
  val tokens: List<BaseToken> = emptyList(),
  val isTokensLoading: Boolean = false,
  val tokenListError: String? = null,
  val tokenFilterQuery: String = "",
  val refreshCountdown: Int = 30,

  // Scanner state
  val scanAddressInput: String = "",
  val isScanning: Boolean = false,
  val activeReport: SecurityReport? = null,
  val scanError: String? = null,

  // History state
  val recentScans: List<SecurityReport> = emptyList()
) {
  val filteredTokens: List<BaseToken>
    get() = if (tokenFilterQuery.isBlank()) {
      tokens
    } else {
      tokens.filter { token ->
        token.name.contains(tokenFilterQuery, ignoreCase = true) ||
          token.symbol.contains(tokenFilterQuery, ignoreCase = true) ||
          token.address.contains(tokenFilterQuery, ignoreCase = true)
      }
    }
}

class BaseShieldViewModel(
  private val repository: BaseShieldRepository = BaseShieldRepository()
) : ViewModel() {

  private val _uiState = MutableStateFlow(BaseShieldUiState())
  val uiState: StateFlow<BaseShieldUiState> = _uiState.asStateFlow()

  val presets: List<QuickScanPreset> = repository.quickScanPresets

  private var timerJob: Job? = null

  init {
    loadTokens(TokenFilterCategory.NEWLY_LAUNCHED)
    startRefreshCountdown()
    // Preload initial scan with a top Base token so scanner tab has an immediate demonstration
    viewModelScope.launch {
      repository.scanToken("0x532f27101965dd16442e59d40670faf5ebb142e4")
        .onSuccess { initialReport ->
          _uiState.update { it.copy(activeReport = initialReport, recentScans = repository.getRecentScans()) }
        }
    }
  }

  fun switchTab(tab: AppDashboardTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun selectCategory(category: TokenFilterCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
    loadTokens(category, forceRefresh = false)
  }

  fun setTokenFilterQuery(query: String) {
    _uiState.update { it.copy(tokenFilterQuery = query) }
  }

  fun setScanAddressInput(input: String) {
    _uiState.update { it.copy(scanAddressInput = input, scanError = null) }
  }

  fun loadTokens(category: TokenFilterCategory, forceRefresh: Boolean = false) {
    viewModelScope.launch {
      _uiState.update { it.copy(isTokensLoading = true, tokenListError = null) }
      val result = repository.fetchBaseTokens(category, forceRefresh)
      result.fold(
        onSuccess = { tokenList ->
          _uiState.update {
            it.copy(
              tokens = tokenList,
              isTokensLoading = false,
              refreshCountdown = 30
            )
          }
        },
        onFailure = { error ->
          _uiState.update {
            it.copy(
              isTokensLoading = false,
              tokenListError = error.localizedMessage ?: "Failed to fetch Base tokens"
            )
          }
        }
      )
    }
  }

  fun scanToken(address: String) {
    val clean = address.trim()
    if (clean.isBlank()) {
      _uiState.update { it.copy(scanError = "Please enter a valid Base contract address (0x...).") }
      return
    }

    _uiState.update {
      it.copy(
        isScanning = true,
        scanError = null,
        scanAddressInput = clean,
        currentTab = AppDashboardTab.SCANNER
      )
    }

    viewModelScope.launch {
      val result = repository.scanToken(clean)
      result.fold(
        onSuccess = { report ->
          _uiState.update {
            it.copy(
              isScanning = false,
              activeReport = report,
              recentScans = repository.getRecentScans(),
              scanError = null
            )
          }
        },
        onFailure = { error ->
          _uiState.update {
            it.copy(
              isScanning = false,
              scanError = error.localizedMessage ?: "Failed to connect to GoPlus Security API."
            )
          }
        }
      )
    }
  }

  fun selectPreset(preset: QuickScanPreset) {
    setScanAddressInput(preset.address)
    scanToken(preset.address)
  }

  private fun startRefreshCountdown() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (isActive) {
        delay(1000)
        _uiState.update { state ->
          val next = state.refreshCountdown - 1
          if (next <= 0) {
            // Auto refresh
            loadTokens(state.selectedCategory, forceRefresh = true)
            state.copy(refreshCountdown = 30)
          } else {
            state.copy(refreshCountdown = next)
          }
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
