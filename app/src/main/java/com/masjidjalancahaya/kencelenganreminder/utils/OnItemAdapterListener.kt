package com.masjidjalancahaya.kencelenganreminder.utils

import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel

interface OnItemAdapterListener {
    fun onPrimaryClick(item: KencelenganModel)
    fun onSecondaryClick(item: KencelenganModel)

    fun onLongPressedClick(item: KencelenganModel)
}