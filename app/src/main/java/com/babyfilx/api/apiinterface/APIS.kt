package com.babyfilx.api.apiinterface


import com.babyfilx.base.App
import com.babyfilx.data.models.PaymentTokenModel
import com.babyfilx.data.models.SelectImageModel
import com.babyfilx.data.models.TokenModel
import com.babyfilx.data.models.UserStatusModel
import com.babyfilx.data.models.response.*
import com.babyfilx.data.models.response.login.Login
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface APIS {


    @POST("server/api/user/login.json")
    @FormUrlEncoded
    suspend fun loginApi(
        @Field("username") email: String,
        @Field("password") password: String
    ): Response<Login>


    @POST("server/services/babyflix/get_my_flix")
    @FormUrlEncoded
    suspend fun galleryApiForHome(@Field("uid") id: String): Response<Any>


    @POST("server/services/babyflix/delete_content")
    @FormUrlEncoded
    suspend fun deleteContentApi(@Field("nid") id: String): Response<Any>


    @POST("server/services/babyflix/upload_content")
    @Multipart
    suspend fun uploadContentApi(
        @Part("uid") id: String,
        @Part("file_name") file_name: String,
        @Part file_chunk: MultipartBody.Part,
    ): Response<Any>


    @POST("server/services/babyflix/update_user_profile")
    suspend fun updateProfileApi(
        @Body params: @JvmSuppressWildcards Map<String, Any>
    ): Response<Any>


    @POST("get_user_company_details/uid/{uid}")
    suspend fun getLocationForUploadContent(
        @Path("uid") uid: String
    ): Response<Any>

    @POST("drupalgap/user/request_new_password")
    @FormUrlEncoded
    suspend fun forgotPasswordApi(
        @Field("name") name: String
    ): Response<Any>


    @GET("getblogs")
    suspend fun blogApiForNews(
        @Query("start") page: Int,

        @Query("record_per_page") record_per_page: Int,
        @Query("search") search: String,
    ): Response<Blog>


    @GET("getblogs/categories")
    suspend fun getBlogsByTheCategories(
        @Query("category_id") category_id: String,
    ): Response<Blog>

    @GET("getblogs/categories")
    suspend fun categoriesApiForNews(): Response<BlogCategory>

    @GET("getLikeStatus")
    suspend fun getLikeBlogsUser(@Query("uid") uid: Long = App.data.id.toLong()): Response<LikesUserModel>

    @FormUrlEncoded
    @POST("server/services/babyflix/add_like.json")
    suspend fun addLikeApi(
        @Field("nid") nid: Long,
        @Field("uid") uid: Long = App.data.id.toLong(),
        @Field("timestamp") timestamp: Long = System.currentTimeMillis() / 1000,
        @Field("vote_source") ipAddress: String,
    ): Response<Any>

    @POST("api/api_firebase")
    suspend fun tokenStore(@Body model: TokenModel): Response<Any>

    @POST("store-device-token")
    suspend fun paymentTokenStore(@Body model: PaymentTokenModel): Response<Any>

    @POST("send-user-plan")
    suspend fun getUserStatus(@Body model: UserStatusModel): Response<UserTypeResponse>

    @POST("aiimage")
    suspend fun enhanceImages(@Body request: SelectImageModel): Response<Any>

    @GET("googlePaymentVerification")
    suspend fun upgradeUser(
        @Query("uid") uid: String,
        @Query("user_plan") userPlan: String,
        @Query("user_plan_type") userPlanType: String,
        @Query("purchase_token") purchaseToken: String,
        @Query("product_id") productId: String
    ): Response<UpgradeUserModel>


}
