package com.mycity4kids.retrofitAPIsInterfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TorcaiAdsAPI {

    @GET("https://publisher.torcai.com/adserver/xchng/MOMS")
    fun getTorcaiAd(
        @Query("adid") adid: String,
        @Query("dm") dm: String,
        @Query("ip") ip: String,
        @Query("appId") appId: String,
        @Query("appName") appName: String,
        @Query("appVersion") appVersion: String,
        @Query("appStoreUrl") appStoreUrl: String,
        @Query("device") device: String,
        @Query("userId") userId: String,
        @Query("appUA") appUA: String
    ): Call<ResponseBody>

    @GET("https://publisher.torcai.com/adserver/xchng/MOMS?adid=rtb_adunit_test_02&dm=www.snaptubeapp.com&ip=103.3.40.138&appId=3&appName=snaptube&appVersion=1&appStoreUrl=http%3A%2F%2Fdl-master.snappea.com%2Finstaller%2Fsnaptube%2Flatest%2FClick_me_to_install_SnapTube_tube_homepage.apk&appUA=Mozilla%2F5.0+(Linux%3B+Android+9%3B+Redmi+Note+5+Pro+Build%2FPKQ1.180904.001%3B+wv)+AppleWebKit%2F537.36+(KHTML%2C+like+Gecko)+Version%2F4.0+Chrome%2F80.0.3987.117+Mobile+Safari%2F537.36")
    fun getTorcaiAd(): Call<ResponseBody>
}
