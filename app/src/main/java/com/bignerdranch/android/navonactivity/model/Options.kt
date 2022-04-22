package com.bignerdranch.android.navonactivity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Options(
    val boxCount:Int,
    val isTimerEnabled:Boolean
):Parcelable{
    companion object{
        val DEFAULT = Options(boxCount = 3, isTimerEnabled = false)
    }
}
//аннотация Parcelize(Parcelable or Serializable) нужна для того что бы передавать в Bundle целые классы,а не кучу ключей и значений
//Serializable занимает гораздо больше места чем следует,поэтому он может не подойти по размеру для максимального размера Bundle
//Parcelize быстрее и меньше весит