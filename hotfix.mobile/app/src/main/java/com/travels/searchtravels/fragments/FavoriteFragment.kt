package com.preview.planner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.travels.searchtravels.R

class FavoriteFragment : AbstractFragment() {

    companion object {
        val TAG: String = FavoriteFragment::class.java.simpleName
        fun newInstance() = FavoriteFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_favorite_fragment, container, false)

        return view
    }




}