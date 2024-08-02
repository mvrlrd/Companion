package ru.mvrlrd.core_impl.network2

import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.mvrlrd.core_api.network.dto.ServerResponse

interface ApiService {
    @POST("foundationModels/v1/completion")
   suspend fun getCompletion(
        @Body requestBody: RequestBody
    ): Response<ServerResponse>
}