package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * GoPlus Security API response wrapper for token security.
 */
@JsonClass(generateAdapter = true)
data class GoPlusResponse(
  @Json(name = "code") val code: Int? = null,
  @Json(name = "message") val message: String? = null,
  @Json(name = "result") val result: Map<String, TokenSecurityRaw>? = null
)

/**
 * Raw security details returned by GoPlus for a token address.
 */
@JsonClass(generateAdapter = true)
data class TokenSecurityRaw(
  @Json(name = "token_name") val tokenName: String? = null,
  @Json(name = "token_symbol") val tokenSymbol: String? = null,
  @Json(name = "total_supply") val totalSupply: String? = null,
  @Json(name = "holder_count") val holderCount: String? = null,

  // 1) Honeypot detection parameters
  @Json(name = "is_honeypot") val isHoneypot: String? = null,
  @Json(name = "cannot_buy") val cannotBuy: String? = null,
  @Json(name = "cannot_sell_all") val cannotSellAll: String? = null,
  @Json(name = "honeypot_with_same_creator") val honeypotWithSameCreator: String? = null,
  @Json(name = "transfer_pausable") val transferPausable: String? = null,
  @Json(name = "is_blacklisted") val isBlacklisted: String? = null,
  @Json(name = "is_whitelisted") val isWhitelisted: String? = null,

  // 2) Liquidity lock status parameters
  @Json(name = "is_in_dex") val isInDex: String? = null,
  @Json(name = "lp_holder_count") val lpHolderCount: String? = null,
  @Json(name = "lp_total_supply") val lpTotalSupply: String? = null,
  @Json(name = "lp_locked") val lpLocked: String? = null,
  @Json(name = "is_locked") val isLocked: String? = null,
  @Json(name = "lp_holders") val lpHolders: List<LpHolderRaw>? = null,

  // 3) Excessive buy/sell taxes parameters
  @Json(name = "buy_tax") val buyTax: String? = null,
  @Json(name = "sell_tax") val sellTax: String? = null,
  @Json(name = "slippage_modifiable") val slippageModifiable: String? = null,
  @Json(name = "personal_slippage_modifiable") val personalSlippageModifiable: String? = null,

  // Additional contract intelligence
  @Json(name = "is_open_source") val isOpenSource: String? = null,
  @Json(name = "is_proxy") val isProxy: String? = null,
  @Json(name = "is_mintable") val isMintable: String? = null,
  @Json(name = "can_take_back_ownership") val canTakeBackOwnership: String? = null,
  @Json(name = "owner_address") val ownerAddress: String? = null,
  @Json(name = "creator_address") val creatorAddress: String? = null,
  @Json(name = "owner_percent") val ownerPercent: String? = null,
  @Json(name = "owner_balance") val ownerBalance: String? = null,
  @Json(name = "hidden_owner") val hiddenOwner: String? = null,
  @Json(name = "selfdestruct") val selfDestruct: String? = null,
  @Json(name = "external_call") val externalCall: String? = null,
  @Json(name = "is_anti_whale") val isAntiWhale: String? = null,
  @Json(name = "anti_whale_modifiable") val antiWhaleModifiable: String? = null
)

@JsonClass(generateAdapter = true)
data class LpHolderRaw(
  @Json(name = "address") val address: String? = null,
  @Json(name = "tag") val tag: String? = null,
  @Json(name = "percent") val percent: String? = null,
  @Json(name = "balance") val balance: String? = null,
  @Json(name = "is_contract") val isContract: Int? = null,
  @Json(name = "is_locked") val isLocked: Int? = null,
  @Json(name = "lp_locked") val lpLocked: Int? = null
)
