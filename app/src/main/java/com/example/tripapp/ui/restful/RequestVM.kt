package com.example.tripapp.ui.restful

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.DeleteDstResponse
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.Poi
import com.ron.restdemo.RetrofitInstance
import okhttp3.MultipartBody


/** 以下都還只是宣告，在coroutineScope呼叫才可使用RetrofitInstance發出API */
class RequestVM : ViewModel() {
    private val tag = "tag_RequestVM"

    /** 仕麟 */
    /** 取得一筆Plan */
    suspend fun GetPlan(id: Int): Plan? {
        try {
            val response = RetrofitInstance.api.GetPlan(id)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }
    /** 取得所有Plan */
    suspend fun GetPlans(): List<Plan> {
        try {
            val response = RetrofitInstance.api.GetPlans()
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }
    /** 寫入一筆Plan */
    suspend fun CreatePlan(request: Plan): Plan? {
        try {
            val response = RetrofitInstance.api.CreatePlan(request)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }
    /** 更新一筆Plan */
    suspend fun UpdatePlan(request: Plan): Plan? {
        try {
            val response = RetrofitInstance.api.UpdatePlan(request)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }
    /** 刪除一筆Plan */
    suspend fun DeletePlan(id: Int): Boolean {
        try {
            val response = RetrofitInstance.api.DeletePlan(id)
            Log.d(tag, "data: ${response}")
            return true
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return false
        }
    }
    /** 取得某張表的行程明細 */
    suspend fun GetDstsBySchedId(id: Int): List<Destination> {
        try {
            val response = RetrofitInstance.api.GetDstsBySchedId(id)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }
    /** 寫入行程明細 */
    suspend fun AddDst(dst: Destination): Destination? {
        try {
            val response = RetrofitInstance.api.CreateDest(dst)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }
    /** 取得所有景點 */
    suspend fun GetPois(): List<Poi> {
        try {
            val response = RetrofitInstance.api.GetPois()
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun GetPlanByMemId(id: Int): List<Plan> {
        try {
            val response = RetrofitInstance.api.GetPlanByMemId(id)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun UpdateDst(dst: Destination): Destination? {
        try {
            val response = RetrofitInstance.api.UpdateDst(dst)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }

    suspend fun GetPlansByContry(contry: String): List<Plan> {
        try {
            val response = RetrofitInstance.api.getPlansByContry(contry)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun GetDestsSample(memId: Int, schId: Int): List<Destination> {
        try {
            val response = RetrofitInstance.api.GetDestsSample(memId, schId)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun GetDestsByDate(date: String): List<Destination> {
        try {
            val response = RetrofitInstance.api.GetDstsByDate(date)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun DeleteDst(id: Int): Boolean {
        try {
            val response = RetrofitInstance.api.DeleteDst(id)
            Log.d(tag, "data: ${response}")
            return true
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return false
        }
    }

    suspend fun CreateCrew(crewMmeber: CrewMmeber): CrewMmeber? {
        try {
            val response = RetrofitInstance.api.CreateCrew(crewMmeber)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }

    suspend fun GetOneOfCrewMember(id: Int): CrewMmeber? {
        try {
            val response = RetrofitInstance.api.GetOneOfCrewMmeber(id)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return null
        }
    }

    suspend fun GetCrewMmebersBySchId(id: Int): List<CrewMmeber> {
        try {
            val response = RetrofitInstance.api.GetCrewMmebers(id)
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun UpdatePostWithImage(image: MultipartBody.Part?) {
        try {
            val response = RetrofitInstance.api.updatePostWithImage(image)
            Log.d(tag, "data: ${response}")
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
        }
    }

    suspend fun GetMembers(): List<Member> {
        try {
            val response = RetrofitInstance.api.getMembers()
            Log.d(tag, "data: ${response}")
            return response
        } catch (e: Exception) {
            Log.e(tag, "error: ${e.message}")
            return emptyList()
        }
    }



    /** 偉峰 --------------------------------------------------------------------------------*/

    /** Ruby --------------------------------------------------------------------------------*/


    /** ㄒㄒ --------------------------------------------------------------------------------*/

    /** 雅勳 --------------------------------------------------------------------------------*/

    /** 喆 --------------------------------------------------------------------------------*/

    /** 致意 --------------------------------------------------------------------------------*/
}