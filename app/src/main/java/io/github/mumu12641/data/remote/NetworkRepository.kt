package io.github.mumu12641.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

class NetworkRepository @Inject constructor(retrofit: Retrofit) {
    private val networkService: NetworkService = retrofit.create(NetworkService::class.java)

    suspend fun uploadWavFile(file: MultipartBody.Part): ResponseBody {
        return networkService.uploadWavFile(file).await()
    }
}