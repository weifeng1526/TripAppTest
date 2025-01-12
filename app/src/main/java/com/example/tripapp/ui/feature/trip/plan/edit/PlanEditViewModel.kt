package com.example.tripapp.ui.feature.trip.plan.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.dataObjects.Poi
import com.example.tripapp.ui.feature.trip.dataObjects.convertSecondsToTimeString
import com.example.tripapp.ui.feature.trip.dataObjects.convertTimeToSeconds
import com.example.tripapp.ui.feature.trip.dataObjects.isDateFormat
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Comparator
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class PlanEditViewModel : ViewModel() {
    val requestVM = RequestVM()

    private var _dstState = MutableStateFlow(Destination())
    val dstState = _dstState.asStateFlow()

    private val _dstsState = MutableStateFlow(emptyList<Destination>())
    val dstsState: StateFlow<List<Destination>> = _dstsState.asStateFlow()

    private val _endTimeMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val endTimeMap: StateFlow<Map<Int, String>> = _endTimeMap

    private val _dstTransferDoneState = MutableStateFlow(emptyList<Destination>())
    val dstTransferDoneState: StateFlow<List<Destination>> = _dstTransferDoneState.asStateFlow()

    private val _dstsForDateState = MutableStateFlow(emptyList<Destination>())
    val dstsForDateState: StateFlow<List<Destination>> = _dstsForDateState.asStateFlow()

    fun setDst(dst: Destination) {
        _dstState.update {
            dst
        }
    }

    fun getDst(): Destination {
        return dstState.value
    }

    fun updateEndTime(index: Int, endTime: String) {
        _endTimeMap.update { currentMap ->
            currentMap.toMutableMap().apply { this[index] = endTime }
        }
    }


    fun addToDses(dst: Destination) {
        _dstsState.update {
            val dsts = it.toMutableList()
            dsts.add(dst)
            dsts
        }
    }

    fun addToDsesByApi(dst: Destination) {
        viewModelScope.launch {
            val response = requestVM.AddDst(dst)
            response?.let {
                addToDses(dst)
                Log.d("addToDsesByApi response", "${response}")
            }
        }
    }

    fun updateDstRquest(dst: Destination) {
        viewModelScope.launch {
            val response = requestVM.UpdateDst(dst)
            Log.d("setDstByApi response", "${response}")
            Log.d("setDstByApi", "${dstState.value}")
        }
    }

    fun setDstByApi(dst: Destination) {
        viewModelScope.launch {
            val response = requestVM.UpdateDst(dst)
            response?.let { setDst(it) }
            Log.d("setDstByApi response", "${response}")
            Log.d("setDstByApi", "${dstState.value}")
        }
    }

    fun setDstsByApi(id: Int) {
        viewModelScope.launch {
            val response = requestVM.GetDstsBySchedId(id)
            setDsts(response)
            Log.d("setDstsByApi response", "${response}")
            Log.d("setDstsByApi", "${dstsState.value}")
        }
    }

    fun updateDstForDateItem(updatedDst: Destination) {
        _dstsForDateState.update { currentList ->
            currentList.map { dst ->
                if (dst.dstNo == updatedDst.dstNo) {
                    updatedDst // 用新的項目替換
                } else {
                    dst // 保持原項目不變
                }
            }
        }
    }

    fun setDsts(dsts: List<Destination>) {
        _dstsState.update {
            dsts
        }
        Log.d("setDstsByApi", "${dstsState.value}")
    }


    fun setDstsForDate(dsts: List<Destination>) {
        _dstsForDateState.update { currentDsts ->
            dstsState.value.filter { destination ->
                dsts.any { it.dstDate == destination.dstDate }
            }
        }
    }

    fun setDstsForDateByApi(date: String) {
        viewModelScope.launch {
            val response = requestVM.GetDestsByDate(date)
            setDstsForDate(response)
            Log.d("setDstsForDateByApi response", "${response}")
            Log.d("setDstsForDateByApi", "${dstsForDateState.value}")
        }
    }

    fun setDstUpSwap(index: Int, dstsForDate: MutableList<Destination>) {
        if (index - 1 >= 0) {
            // 交換屬性
            val tempStart = dstsForDate[index].dstStart
            dstsForDate[index].dstStart = dstsForDate[index - 1].dstStart
            dstsForDate[index - 1].dstStart = tempStart

            val tempStay = dstsForDate[index].dstEnd
            dstsForDate[index].dstEnd = dstsForDate[index - 1].dstEnd
            dstsForDate[index - 1].dstEnd = tempStay

            val tempTransfer = dstsForDate[index].dstInr
            dstsForDate[index].dstInr = dstsForDate[index - 1].dstInr
            dstsForDate[index - 1].dstInr = tempTransfer

            _dstsForDateState.update {
                it.sortedBy { it.dstStart }
            }

            dstsForDateState.value.forEach {
                setDstByApi(it)
            }

            //            val tempPoiNo = dstsForDate[index].poiNo
//            dstsForDate[index].poiNo = dstsForDate[index - 1].poiNo
//            dstsForDate[index - 1].poiNo = tempPoiNo
//
//            val tempDstName = dstsForDate[index].dstName
//            dstsForDate[index].dstName = dstsForDate[index - 1].dstName
//            dstsForDate[index - 1].dstName = tempDstName
//
//            val tempDstAddr = dstsForDate[index].dstAddr
//            dstsForDate[index].dstAddr = dstsForDate[index - 1].dstAddr
//            dstsForDate[index - 1].dstAddr = tempDstAddr
//
//            val tempDstPic = dstsForDate[index].dstPic
//            dstsForDate[index].dstPic = dstsForDate[index - 1].dstPic
//            dstsForDate[index - 1].dstPic = tempDstPic
//
//            val tempDstDep = dstsForDate[index].dstDep
//            dstsForDate[index].dstDep = dstsForDate[index - 1].dstDep
//            dstsForDate[index - 1].dstDep = tempDstDep
        }
    }

    //資料向下交換
    fun setDstDownSwap(index: Int, dsts: MutableList<Destination>) {
        if (index + 1 < dsts.size) {
            val tempStart = dsts[index].dstStart
            dsts[index].dstStart = dsts[index + 1].dstStart
            dsts[index + 1].dstStart = tempStart

            val tempStay = dsts[index].dstEnd
            dsts[index].dstEnd = dsts[index + 1].dstEnd
            dsts[index + 1].dstEnd = tempStay

            val tempTransfer = dsts[index].dstInr
            dsts[index].dstInr = dsts[index + 1].dstInr
            dsts[index + 1].dstInr = tempTransfer

            val newDsts = dsts.toMutableList()
            val current = dsts[index]
            val next = dsts[index + 1]

            newDsts[index + 1] = current
            newDsts[index] = next

            _dstsForDateState.update { newDsts }
            Log.d("newDst", "${_dstsForDateState.value}")
           // newDsts.forEach { setDstByApi(it) }

            // 打 api


        }
    }

//    /* 以下是某天的行程明細的MutableStateFlow **/
//    fun setDstsForDate(date: String) {
//        val dstsFordate = _dstsState.value.filter { it.dstDate.equals(date) }
//        _dstsForDateState.update {
//            dstsFordate
//        }
//    }

    fun addToDstForDate(dst: Destination) {
        _dstsForDateState.update {
            val dstsFordate = it.toMutableList()
            dstsFordate.add(dst)
            dstsFordate
        }
    }

    //使用者操作情境
    //1. 調整B停留時間，B計算加長結束時間，影響C開始時間，後續DEF時間都會等量調整
    //2. 調整B轉移時間，B計算加長結束時間，影響C開始時間，後續DEF時間都會等量調整
    //3. 調整B開始時間，B計算加長結束時間，影響C開始時間，後續DEF時間都會等量調整
    //3. 調整C開始時間，卻落在A的開始-B的開始之間，插入B的下方，後續處理跟1.2.一樣
    fun onStartTimeChange() {
        _dstsForDateState.update { dsts ->
            dsts.sortedBy { it.dstStart }
        }
    }

    fun removeDsts(id: Int) {
        _dstsState.update {
            val dsts = it.toMutableList()
            dsts.removeIf { id == it.dstNo }
            dsts
        }
    }

    fun removeDstsForDate(id: Int) {
        _dstsForDateState.update {
            val dstsForDate = it.toMutableList()
            dstsForDate.removeIf { id == it.dstNo }
            dstsForDate
        }
    }

    fun deleteDstByApi(dst: Destination) {
        var response = false
        viewModelScope.launch {
            response = requestVM.DeleteDst(dst.dstNo)
            Log.d("deleteDst", "${response}")
            if (response) {
                removeDsts(dst.dstNo)
                removeDstsForDate(dst.dstNo)
            }
        }
    }

    fun onAddDstWhenPoiSelect(poi: Poi, schNo: Int, selectedDate: String) {
        val newDst = Destination()
        //拿到poi寫到dest
        newDst.schNo = schNo
        newDst.poiNo = poi.poiNo
        newDst.dstName = poi.poiName
        newDst.dstAddr = poi.poiAdd
        newDst.dstPic = ByteArray(0)
        newDst.dstDep = "新增了一個景點"
        newDst.dstDate = selectedDate
        newDst.dstStart = "01:00:00"
        newDst.dstEnd = "00:00:00"
        newDst.dstInr = "00:00:00"
        viewModelScope.launch {
            val response = requestVM.AddDst(newDst)
            response?.let {
                addToDses(newDst)
                addToDstForDate(newDst)
                Log.d("addToDsesByApi response", "${response}")
            }
        }
        //直接排在最後，我再判斷，也讓使用者排
    }
}