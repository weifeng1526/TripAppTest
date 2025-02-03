package com.ron.restdemo

//import com.example.tripapp.ui.feature.trip.plan.restful.CreatePlan
import com.example.tripapp.ui.feature.baggage.BagItems
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
import com.example.tripapp.ui.feature.spending.CrewRecord
import com.example.tripapp.ui.feature.spending.PostSpendingRecord
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.DeleteDstResponse
import okhttp3.MultipartBody
import retrofit2.Response
import com.example.tripapp.ui.feature.trip.dataObjects.Notes
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("sched/getAllFromCrew/mem_id")
    suspend fun GetPlansOfMemberInCrew(@Query("id") id: Int): List<Plan>

    @POST("sched/create")
    suspend fun CreatePlan(@Body request: Plan): Plan

    @POST("sched/dest/create")
    suspend fun CreateDest(@Body request: Destination): Destination

    @POST("sched/crew/create")
    suspend fun CreateCrew(@Body request: CrewMmeber): CrewMmeber

    @GET("sched/crew/get_one")
    suspend fun GetOneOfCrewMmeber(@Query("crewMemberId") id: Int): CrewMmeber

//    @GET("sched/crew/getBySchId")
//    suspend fun GetCrewMmebers(@Query("schId") id: Int): List<CrewMmeber>
    @GET("sched/memberInCrew/getBySchId")
    suspend fun GetCrewMmebers(@Query("schId") id: Int): List<CrewMmeber>

    @PUT("sched/crew/update")
    suspend fun UpdateCrewMmeber(@Body request: CrewMmeber): CrewMmeber

    @PUT("sched/update")
    suspend fun UpdatePlan(@Body request: Plan): Plan

    @DELETE("sched/delete")
    suspend fun DeletePlan(@Query("id") id: Int): DeletePlanResponse

    @DELETE("sched/dest/delete")
    suspend fun DeleteDst(@Query("id") id: Int): DeleteDstResponse

    @GET("sched/get_dests")
    suspend fun GetDstsBySchedId(@Query("id") id: Int): List<Destination>

    @GET("sched/getDestByDate")
    suspend fun GetDstsByDate(@Query("date") date: String): List<Destination>

    @GET("sched/get_all/sch_con")
    suspend fun getPlansByContry(@Query("name") name: String): List<Plan>

    @GET("sched/poi/get_all")
    suspend fun GetPois(): List<Poi>

    @GET("sched/get_all/mem_id")
    suspend fun GetPlanByMemId(@Query("id") id: Int): List<Plan>

    @PUT("sched/dest/update")
    suspend fun UpdateDst(@Body request: Destination): Destination

    @GET("sched/getDestsSample")
    suspend fun GetDestsSample(@Query("memId") memId: Int, @Query("schId") schId: Int): List<Destination>

//    @Multipart
//    @PUT("sched/image")
//    suspend fun UpdatePlanImage(
//        @Part image: MultipartBody.Part?
//    ): Response<Unit>

    @Multipart
    @PUT("sched/image")
    suspend fun UpdatePlanImage(
        @Part ("schId") schId: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Unit>


    @GET("sched/member/get_all")
    suspend fun getMembers(): List<Member> // 借用




    //緯風
    @POST("member/login")
    suspend fun login(@Body request: LoginRequest): Member

    @POST("member/signup")
    suspend fun signup(@Body request: SignUpRequest): Member

    //ㄒㄒ

    @GET("item/get")
    suspend fun GetItems(): List<Item>

    @GET("bag/getitems")
    suspend fun GetBagItems(
        @Query("memNo") memNo: Int,
        @Query("schNo") schNo: Int
    ): List<BagList>

    @GET("bag/getitemsbyschno")
    suspend fun GetBagItemsBySchNo(
        @Query("schNo") schNo: Int
    ): List<BagItems>

    @GET("item/getexist")
    suspend fun GetItemsIfExist(
        @Query("memNo") memNo: Int, @Query("schNo") schNo: Int
    ):List<Item>

    @POST("bag/add")
    suspend fun AddBagItem(@Body bagListEntry: BagList): List<BagList>

    @DELETE("bag/delete")
    suspend fun DeleteBagItem(@Query("memNo")memNo: Int,@Query("schNo")schNo: Int,@Query("itemNo")itemNo: Int):List<Item>

    @PUT("bag/update")
    suspend fun UpdateReadyStatus(@Body bagList: BagList): Response<Unit>

    //盧比
    //1 呼叫API
    @GET("spending/findTripsSpendingAll") //根據會員編號找尋消費明細
    suspend fun getSpendingList(@Query("memNo") memNo: Int): List<SpendingRecord>

    @GET("spending/findOneTripsSpending")  //根據消費明細編號尋找單筆消費
    suspend fun getOneSpendingList(@Query("costNo") costNo: Int): SpendingRecord

    @GET("spending/findTripCrew")  //根據行程編號找團員
    suspend fun findTripCrew(@Query("schNo") schNo: Int): List<CrewRecord>?

    @GET("spending/findTripName") //根據會員編號找到旅程名稱
    suspend fun findTripName(@Query("memNo") memNo:Int): List<CrewRecord>

    @GET("spending/findTripCur") //根據行程編號找到旅行幣別與結算幣別
    suspend fun findTripCur(@Query("schNo") schNo:Int): List<CrewRecord>

    @POST("spending/addlistController") //新增消費明細編資料insert
    //丟的內容要跟後端設定的一致 request:PostSpendingRecord（類別：規格）
    suspend fun addlistController(@Body request:PostSpendingRecord)

    @POST("spending/saveOneTripsSpending") //修改消費明細編資料update
    //丟的內容要跟後端設定的一致 request:PostSpendingRecord（類別：規格）
    //fetch(API) / get(本地端--偏好設定) 都是在接api的代名詞
    suspend fun saveOneTripsSpending(@Body request:PostSpendingRecord)

    @GET("spending/removeOneTripsSpending") //刪除該筆紀錄
    suspend fun removeOneTripsSpending(@Query("costNo") costNo:Int)




    //雅勳
    //陶喆
    @GET("notes/dstnotes")
    suspend fun GetNotes(@Query("dstNo") dstNo: Int, @Query("memNo") memNo: Int): Notes

    @POST("notes/update")
    suspend fun UpdateNotes(@Body request: Notes): Notes

    @POST("notes/creat")
    suspend fun CreateNotes(@Body request: Notes): Notes

    @GET("notes/getImage")
    suspend fun GetImage(@Query("dstNo") dstNo: Int) : Destination
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