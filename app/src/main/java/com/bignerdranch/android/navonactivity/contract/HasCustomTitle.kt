package com.bignerdranch.android.navonactivity.contract

import androidx.annotation.StringRes


//Если фрагмент реализует этот интерфейс,это значит что он поддерживает заголовок экрана
interface HasCustomTitle {

    @StringRes
    fun getTitleRes():Int
}