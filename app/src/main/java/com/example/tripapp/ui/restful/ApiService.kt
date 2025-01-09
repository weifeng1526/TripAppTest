package com.ron.restdemo

//import com.example.tripapp.ui.feature.trip.plan.restful.CreatePlan
import com.example.tripapp.ui.feature.spending.SpendingRecord
import com.example.tripapp.ui.feature.baggage.BagList
import com.example.tripapp.ui.feature.baggage.Item
import com.example.tripapp.ui.feature.trip.dataObjects.DeletePlanResponse
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.Poi
import com.example.tripapp.ui.feature.member.LoginRequest
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.member.SignUpRequest
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

//提供給使用者一個ApiService介面，底下自己定義各種RESTFUL抽象方法
interface ApiService {
//    @POST("/sched/add")
//    註記這個request變數為Http Request Body
//    該變數型態就是body的資料物件(Object Convert to Json)
//    此抽象方法的回傳值是Http Response Body的資料物件(From Json Convert to Object)
//    suspend fun CreatePlan(@Body request: CreatePlanRequest): PlanResponse

    //仕麟
    @GET("sched/get_one")
    suspend fun GetPlan(@Query("id") id: Int): Plan

    @GET("sched/get_all")
    suspend fun GetPlans(): List<Plan>

    @POST("sched/create")
    suspend fun CreatePlan(@Body request: Plan): Plan

    @POST("sched/dest/create")
    suspend fun CreateDest(@Body request: Destination): Destination

    @PUT("sched/update")
    suspend fun UpdatePlan(@Body request: Plan): Plan

    @DELETE("sched/delete")
    suspend fun DeletePlan(@Query("id") id: Int): DeletePlanResponse

    @GET("sched/get_dests")
    suspend fun GetDstsBySchedId(@Query("id") id: Int): List<Destination>

    @GET("sched/get_all/sch_con")
    suspend fun getPlansByContry(@Query("name") name: String): List<Plan>

    @GET("sched/poi/get_all")
    suspend fun GetPois(): List<Poi>

    @GET("sched/get_all/mem_id")
    suspend fun GetPlanByMemId(@Query("id") id: Int): List<Plan>

    @GET("sched/dest/get_last")
    suspend fun GetLastDst(): Destination

    @PUT("sched/dest/update")
    suspend fun UpdateDst(@Body request: Destination): Destination

    @GET("sched/getDestsSample")
    suspend fun GetDestsSample(@Query("memId") memId: Int, @Query("schId") schId: Int): List<Destination>





    //緯風
    @POST("member/login")
    suspend fun login(@Body request: LoginRequest): Member

    @POST("member/signup")
    suspend fun signup(@Body request: SignUpRequest): Member

    //ㄒㄒ
//    @GET("item/get_id")
//    suspend fun GetItem(@Query("id") id: Int): Item // 修改為返回單一 Item

    @GET("item/get")
    suspend fun GetItems(): List<Item>

    @GET("bag/get")
    suspend fun  GetBagLists(): List<BagList>


    //盧比
    @GET("spending/findTripsSpendingAll")
    suspend fun getSpendingList(): List<SpendingRecord>
    //雅勳
    //陶喆
    //致意
}



//單例RetrofitInstance
//api型態為ApiService介面，採用by lazy延遲初始化(呼叫才會實例化)
//這裡是實例化Retrofit並建立BaseURL，使用GSON函式來轉換Json格式
//主要是@簡化了Json<-->Object序列化、反序列化的過程
object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/TripAppEnd/") // Base URL
            .addConverterFactory(GsonConverterFactory.create()) // GSON for JSON conversion
            .build()
            .create(ApiService::class.java)
    }
}