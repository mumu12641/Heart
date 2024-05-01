package io.github.mumu12641.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface NetworkService {
    @Multipart
    @POST("upload")
//    fun uploadWavFile(@PartMap files: Map<String, @JvmSuppressWildcards RequestBody>): Call<ResponseBody>
    fun uploadWavFile(@Part file: MultipartBody.Part): Call<ResponseBody>
}