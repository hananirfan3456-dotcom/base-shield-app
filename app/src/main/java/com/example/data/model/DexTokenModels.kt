package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Locale

/**
 * Unified model for Base Network (Chain ID: 8453) tokens.
 */
data class BaseToken(
  val address: String,
  val name: String,
  val symbol: String,
  val priceUsd: Double,
  val priceChange24h: Double,
  val volume24hUsd: Double,
  val fdvUsd: Double,
  val imageUrl: String? = null,
  val poolAddress: String? = null,
  val dexName: String = "Base DEX",
  val createdAt: String? = null,
  val isNewlyLaunched: Boolean = false,
  val chainId: String = "8453"
) {
  val formattedPrice: String
    get() = when {
      priceUsd >= 1.0 -> String.format(Locale.US, "$%,.2f", priceUsd)
      priceUsd >= 0.0001 -> String.format(Locale.US, "$%.4f", priceUsd)
      priceUsd > 0.0 -> String.format(Locale.US, "$%.8f", priceUsd)
      else -> "$0.00"
    }

  val formattedChange24h: String
    get() = String.format(Locale.US, "%+.2f%%", priceChange24h)

  val formattedVolume: String
    get() = when {
      volume24hUsd >= 1_000_000 -> String.format(Locale.US, "$%.1fM", volume24hUsd / 1_000_000)
      volume24hUsd >= 1_000 -> String.format(Locale.US, "$%.1fK", volume24hUsd / 1_000)
      volume24hUsd > 0 -> String.format(Locale.US, "$%.0f", volume24hUsd)
      else -> "$0"
    }

  val truncatedAddress: String
    get() = if (address.length >= 10) {
      "${address.take(6)}...${address.takeLast(4)}"
    } else address
}

// -------------------------------------------------------------
// GeckoTerminal API Response Models
// -------------------------------------------------------------
@JsonClass(generateAdapter = true)
data class GeckoTerminalPoolListResponse(
  @Json(name = "data") val data: List<GeckoPoolData>? = null,
  @Json(name = "included") val included: List<GeckoIncludedData>? = null
)

@JsonClass(generateAdapter = true)
data class GeckoPoolData(
  @Json(name = "id") val id: String? = null,
  @Json(name = "type") val type: String? = null,
  @Json(name = "attributes") val attributes: GeckoPoolAttributes? = null,
  @Json(name = "relationships") val relationships: GeckoPoolRelationships? = null
)

@JsonClass(generateAdapter = true)
data class GeckoPoolAttributes(
  @Json(name = "name") val name: String? = null,
  @Json(name = "address") val address: String? = null,
  @Json(name = "base_token_price_usd") val baseTokenPriceUsd: String? = null,
  @Json(name = "fdv_usd") val fdvUsd: String? = null,
  @Json(name = "pool_created_at") val poolCreatedAt: String? = null,
  @Json(name = "volume_usd") val volumeUsd: Map<String, String>? = null,
  @Json(name = "price_change_percentage") val priceChangePercentage: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class GeckoPoolRelationships(
  @Json(name = "base_token") val baseToken: GeckoRelationshipItem? = null,
  @Json(name = "quote_token") val quoteToken: GeckoRelationshipItem? = null,
  @Json(name = "dex") val dex: GeckoRelationshipItem? = null
)

@JsonClass(generateAdapter = true)
data class GeckoRelationshipItem(
  @Json(name = "data") val data: GeckoRelationshipData? = null
)

@JsonClass(generateAdapter = true)
data class GeckoRelationshipData(
  @Json(name = "id") val id: String? = null,
  @Json(name = "type") val type: String? = null
)

@JsonClass(generateAdapter = true)
data class GeckoIncludedData(
  @Json(name = "id") val id: String? = null,
  @Json(name = "type") val type: String? = null,
  @Json(name = "attributes") val attributes: GeckoTokenAttributes? = null
)

@JsonClass(generateAdapter = true)
data class GeckoTokenAttributes(
  @Json(name = "address") val address: String? = null,
  @Json(name = "name") val name: String? = null,
  @Json(name = "symbol") val symbol: String? = null,
  @Json(name = "image_url") val imageUrl: String? = null,
  @Json(name = "decimals") val decimals: Int? = null
)

// -------------------------------------------------------------
// DexScreener API Response Models
// -------------------------------------------------------------
@JsonClass(generateAdapter = true)
data class DexScreenerSearchResponse(
  @Json(name = "schemaVersion") val schemaVersion: String? = null,
  @Json(name = "pairs") val pairs: List<DexPair>? = null
)

@JsonClass(generateAdapter = true)
data class DexPair(
  @Json(name = "chainId") val chainId: String? = null,
  @Json(name = "dexId") val dexId: String? = null,
  @Json(name = "pairAddress") val pairAddress: String? = null,
  @Json(name = "baseToken") val baseToken: DexToken? = null,
  @Json(name = "quoteToken") val quoteToken: DexToken? = null,
  @Json(name = "priceUsd") val priceUsd: String? = null,
  @Json(name = "priceChange") val priceChange: DexPriceChange? = null,
  @Json(name = "volume") val volume: DexVolume? = null,
  @Json(name = "fdv") val fdv: Double? = null,
  @Json(name = "pairCreatedAt") val pairCreatedAt: Long? = null,
  @Json(name = "info") val info: DexPairInfo? = null
)

@JsonClass(generateAdapter = true)
data class DexToken(
  @Json(name = "address") val address: String? = null,
  @Json(name = "name") val name: String? = null,
  @Json(name = "symbol") val symbol: String? = null
)

@JsonClass(generateAdapter = true)
data class DexPriceChange(
  @Json(name = "m5") val m5: Double? = null,
  @Json(name = "h1") val h1: Double? = null,
  @Json(name = "h6") val h6: Double? = null,
  @Json(name = "h24") val h24: Double? = null
)

@JsonClass(generateAdapter = true)
data class DexVolume(
  @Json(name = "h24") val h24: Double? = null,
  @Json(name = "h6") val h6: Double? = null,
  @Json(name = "h1") val h1: Double? = null,
  @Json(name = "m5") val m5: Double? = null
)

@JsonClass(generateAdapter = true)
data class DexPairInfo(
  @Json(name = "imageUrl") val imageUrl: String? = null
)
