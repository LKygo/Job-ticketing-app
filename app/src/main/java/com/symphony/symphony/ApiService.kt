package com.symphony.symphony

import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

class ApiService {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody
    ): Call<ApiResponse> {
    }

}

    class ApiResponse(message: JSONObject) {

        private var message: Any

        init {
            this.message = message
        }
    }


