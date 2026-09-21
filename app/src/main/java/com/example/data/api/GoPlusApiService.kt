package com.example.data.api

import com.example.data.model.GoPlusResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Free GoPlus Security API client for real-time contract audits on Base Network (Chain ID: 8453).
 */
interface GoPlusApiService {

  @GET("api/v1/token_security/{chainId}")
  suspend fun getTokenSecurity(
    @Path("chainId") chainId: String = "8453",
    @Query("contract_addresses") contractAddresses: String
  ): GoPlusResponse
}
