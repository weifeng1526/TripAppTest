package com.example.tripapp.ui.feature.member.turfav

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TurFavViewModel: ViewModel() {
    private val _turFavState = MutableStateFlow (emptyList<TurFav>())
    val turFavState:StateFlow<List<TurFav>> = _turFavState.asStateFlow()

    init {
        _turFavState.update { fetchTurFav() }
    }
        //之後再改下面的
//    private val _favListState = MutableStateFlow (Fav())
//    val favListState:StateFlow<Fav> = _favListState.asStateFlow()
//    fun setFavListState(fav: Fav){
//        _favListState.updateAndGet {
//            fav
//        }
//    }
}

private fun fetchTurFav() : List<TurFav> {
    return listOf(
        TurFav(1, "我的口袋景點"),
        TurFav(2, "我的口袋景點2"),
        TurFav(3, "我的口袋景點3"),
        TurFav(4, "我的口袋景點4"),
        TurFav(5, "我的口袋景點5"),
    )
}

data class TurFav (var tfFolderNo: Int, val tfFolderName: String = "" ) {
    override fun equals(other: Any?): Boolean {
        return this.tfFolderNo == (other as TurFav).tfFolderNo
    }
}
