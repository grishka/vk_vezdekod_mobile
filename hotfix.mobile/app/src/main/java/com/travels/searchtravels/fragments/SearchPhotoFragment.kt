package com.preview.planner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.travels.searchtravels.R


class SearchPhotoFragment : AbstractFragment() {

    companion object {
        val TAG: String = SearchPhotoFragment::class.java.simpleName
        fun newInstance() = SearchPhotoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_photo_search_fragment, container, false)

        return view
    }





}