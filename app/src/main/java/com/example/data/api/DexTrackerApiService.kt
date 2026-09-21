package com.example.data.api

import com.example.data.model.DexScreenerSearchResponse
import com.example.data.model.GeckoTerminalPoolListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Free Decentralized Exchange Tracker APIs for Base Network (Chain ID: 8453).
 */
interface GeckoTerminalApiService {

  @GET("api/v2/networks/base/new_pools")
  suspend fun getNewPools(
    @Query("include") include: String = "base_token"
  ): GeckoTerminalPoolListResponse

  @GET("api/v2/networks/base/trending_pools")
  suspend fun getTrendingPools(
    @Query("include") include: String = "base_token"
  ): GeckoTerminalPoolListResponse

  @GET("api/v2/networks/base/pools")
  suspend fun getActivePools(
    @Query("include") include: String = "base_token",
    @Query("page") page: Int = 1
  ): GeckoTerminalPoolListResponse
}

interface DexScreenerApiService {

  @GET("latest/dex/search")
  suspend fun searchPairs(
    @Query("q") query: String
  ): DexScreenerSearchResponse

  @GET("latest/dex/tokens/{tokenAddresses}")
  suspend fun getTokens(
    @Path("tokenAddresses") tokenAddresses: String
  ): DexScreenerSearchResponse
}
