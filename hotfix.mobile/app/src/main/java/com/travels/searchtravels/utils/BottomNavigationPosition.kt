package com.travels.searchtravels.utils

import androidx.fragment.app.Fragment
import com.preview.planner.fragments.FavoriteFragment
import com.preview.planner.fragments.SearchFragment
import com.preview.planner.fragments.SearchPhotoFragment
import com.travels.searchtravels.R


enum class BottomNavigationPosition(val position: Int, val id: Int) {
    PREVIEW(0, R.id.search),
    PLANNER(1, R.id.searchPhoto),
    PROMOTION(2, R.id.favorite)
}

fun findNavigationPositionById(id: Int): BottomNavigationPosition = when (id) {
    BottomNavigationPosition.PREVIEW.id -> BottomNavigationPosition.PREVIEW
    BottomNavigationPosition.PLANNER.id -> BottomNavigationPosition.PLANNER
    BottomNavigationPosition.PROMOTION.id -> BottomNavigationPosition.PROMOTION


    //BottomNavigationPosition.PROFILE.id -> BottomNavigationPosition.PROFILE
    else -> BottomNavigationPosition.PREVIEW
}

fun BottomNavigationPosition.createFragment(): Fragment = when (this) {
    BottomNavigationPosition.PREVIEW -> SearchFragment.newInstance()
    BottomNavigationPosition.PLANNER -> SearchPhotoFragment.newInstance()
    BottomNavigationPosition.PROMOTION -> FavoriteFragment.newInstance()

}

fun BottomNavigationPosition.getTag(): String = when (this) {
    BottomNavigationPosition.PREVIEW -> SearchFragment.TAG
    BottomNavigationPosition.PLANNER -> SearchPhotoFragment.TAG
    BottomNavigationPosition.PROMOTION -> FavoriteFragment.TAG

}