package com.bignerdranch.android.navonactivity.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bignerdranch.android.navonactivity.model.Options

typealias ResultListener<T> = (T) ->Unit


//расширение,нужно для того что бы каждый фрагмент мог получить доступ к навигатору
//активити внутри которой распологаются фрагменты и будет навигатором
fun Fragment.navigator():Navigator{
    return requireActivity() as Navigator
}

interface Navigator{
    //Показ экранов
    fun showBoxSelectionScreen(options:Options)

    fun showOptionsScreen(options: Options)

    fun showAboutScreen()

    fun showCongratulationsScreen()

    //Выход обратно или в мен

    fun goBack()

    fun goToMenu()

    //Публикация результатов из текущего экрана

    fun <T:Parcelable> publishResult(result:T)

    //Прослушивание результатов из другого экрана
    //Слушаем результат по ключу,т.е по названию класса
    fun <T:Parcelable> listenResult(clazz: Class<T>,owner:LifecycleOwner,listener: ResultListener<T>)
}