package com.example.tripapp.ui.feature.member.turfav.favlist

import androidx.lifecycle.ViewModel
import com.example.tripapp.ui.feature.member.turfav.TurFav
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavListViewModel: ViewModel() {
    private val _favListState = MutableStateFlow (emptyList<fetchFav>())
    val favListState: StateFlow<List<fetchFav>> = _favListState.asStateFlow()

    init {
        _favListState.update { fetchFav() }
    }
}

private fun fetchFav() : List<fetchFav> {
    return listOf(
        fetchFav(1,1, "景點"),
        fetchFav(1,2, "景點2"),
        fetchFav(2,3, "景點3"),
        fetchFav(2,4, "景點4"),
        fetchFav(3,5, "景點5"),
    )
}

data class fetchFav (val folderNo: Int ,var poiNo: Int, val poiName: String = "" ) {
    override fun equals(other: Any?): Boolean {
        return this.poiNo == (other as fetchFav).poiNo
    }
}