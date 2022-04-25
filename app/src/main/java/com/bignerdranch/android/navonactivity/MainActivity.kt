package com.bignerdranch.android.navonactivity

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.bignerdranch.android.navonactivity.contract.*
import com.bignerdranch.android.navonactivity.databinding.ActivityMainBinding
import com.bignerdranch.android.navonactivity.model.Options

//вся реализация Navigator находится в активити

open class MainActivity:AppCompatActivity(),Navigator {

    private lateinit var binding:ActivityMainBinding

    //Вернёт фрагмент который сейчас находится в контейнере,т.е текущий
    private val  currentFragment:Fragment
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!

    //как только собирается показаться интерфейс нового фрагмента,значит он становится текущим,вызывает метод обновления интерфейса
    private val fragmentListener = object :FragmentManager.FragmentLifecycleCallbacks(){
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUi()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setSupportActionBar(binding.toolbar)

        //Если активити запущена в первый раз,то мы создаём фрагмент меню
        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer,MenuFragment())
                .commit()
        }
        //Опционально
        //Регистрируем коллбэк,лиссенер выше,будет вызыватся тогда,когда создался интерфейс для определённого фрагмента
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener,false)
    }


    override fun onDestroy() {
        super.onDestroy()
        //отрегистрируем коллбэк
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //Создаём бандл,запихиваем в него то что нужно,возвращаем фрагмент и кладём ему аргумент,всё по канонам
    //а достаём это в самом фрагменте
    override fun showBoxSelectionScreen(options: Options) {
        launchFragment(BoxSelectionFragment.newInstance(options))
    }

    override fun showOptionsScreen(options: Options) {
        launchFragment(OptionsFragment.newInstance(options))
    }

    override fun showAboutScreen() {
        launchFragment(AboutFragment())
    }

    override fun showCongratulationsScreen() {
        launchFragment(BoxFragment())
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun goToMenu() {
        //убирает все фрагменты которые находятся в бэкстеке,кроме изначального(меню)
        supportFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    //Метод setFragmentResult предназначен для того что бы посылать результат работы фрагмента в другие фрагменты
    //1 парам-ключ результата,2 парам-сам результат
    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(result.javaClass.name, bundleOf(KEY_RESULT to result))
    }

    //Метод setFragmentResultListener - это слушатель
    //1-класс результата который слушаем,овнер и лисенер
    //Здесь мы передаём всё что получили в лисенер который передали
    override fun <T : Parcelable> listenResult(clazz: Class<T>, owner: LifecycleOwner, listener: ResultListener<T>) {
        supportFragmentManager.setFragmentResultListener(clazz.name,owner, FragmentResultListener{key,bundle ->
            listener.invoke(bundle.getParcelable(KEY_RESULT)!!)
        })
    }

    private fun launchFragment(fragment:Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer,fragment)
            .commit()
    }

    //обновление тулбара
    private fun updateUi() {
        val fragment = currentFragment

        //Если фрагмент реализует интерфейс,то мы вытягиваем нужный текст и присваиваем
        //а если нет,то заголовок по умолчанию
        if(fragment is HasCustomTitle){
            binding.toolbar.title = getString(fragment.getTitleRes())
        } else{
            binding.toolbar.title = getString(R.string.fragment_navigation_example)
        }

        //если в бэкстеке есть хотя бы 1 фрагмент,то нужно показать кнопку назад(меню в нём нет)
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        //если фрагмент реализует интерфейс с действиями,то мы создаём экшен через createCustomToolbarAction
        if (fragment is HasCustomAction) {
            createCustomToolbarAction(fragment.getCustomAction())
        } else {
            binding.toolbar.menu.clear()
        }
    }

    //Создаём картинку,назначаем белый цвет
    //создаём меню item,назначаем текст и иконку,показываем как действие в тулбаре
    //добавляем слушатель,в нём запускаем действие которое указали через run
    private fun createCustomToolbarAction(action: CustomAction) {
        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.WHITE)

        val menuItem = binding.toolbar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    companion object {
        private const val KEY_RESULT = "RESULT"
    }
}