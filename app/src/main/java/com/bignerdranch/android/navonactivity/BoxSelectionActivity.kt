package com.bignerdranch.android.navonactivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.bignerdranch.android.navonactivity.databinding.ActivityBoxSelectionBinding
import com.bignerdranch.android.navonactivity.databinding.ItemBoxBinding
import com.bignerdranch.android.navonactivity.model.Options
import kotlin.IllegalStateException
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.math.max

class BoxSelectionActivity : BaseActivity() {

    private lateinit var binding:ActivityBoxSelectionBinding

    private lateinit var options:Options

    private lateinit var timer: CountDownTimer

    private var timerStartTimestamp by Delegates.notNull<Long>()
    private var boxIndex by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        options = intent.getParcelableExtra(EXTRA_OPTIONS)?:throw IllegalStateException("Can't launch BoxSelectionActivity without options")
        //генерируем ящик победитель
        boxIndex = savedInstanceState?.getInt(KEY_INDEX)?: Random.nextInt(options.boxCount)

        if(options.isTimerEnabled){
            timerStartTimestamp = savedInstanceState?.getLong(KEY_START_TIMESTAMP)?:System.currentTimeMillis()
            setupTimer()
            updateTimerUi()
        }
        createBoxes()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX,boxIndex)
        if(options.isTimerEnabled){
            outState.putLong(KEY_START_TIMESTAMP,timerStartTimestamp)
        }
    }
    //Таймер,это асинхронная операция,поэтому запуск и остановка таймера будут в start and stop
    override fun onStart() {
        super.onStart()
        if(options.isTimerEnabled){
            timer.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if(options.isTimerEnabled){
            timer.cancel()
        }
    }

    private fun setupTimer() {
        //Таймер асбтрактный,нужно создать анонимный объект и реализовать методы
        timer = object :CountDownTimer(getRemainingSeconds()*1000,1000){
            override fun onFinish() {
                updateTimerUi()
                showTimerEndDialog()
            }

            override fun onTick(millisUntilFinished: Long) {
                updateTimerUi()
            }
        }
    }


    private fun updateTimerUi() {
        if(getRemainingSeconds() >= 0){
            binding.timerTextView.visibility = View.VISIBLE
            binding.timerTextView.text = getString(R.string.timer_value,getRemainingSeconds())
        }else{
            binding.timerTextView.visibility = View.GONE
        }
    }

    private fun getRemainingSeconds(): Long {
        val finishedAt = timerStartTimestamp + TIMER_DURATION
        return max(0,(finishedAt - System.currentTimeMillis())/1000)
    }

    //создаём диалог,настраиваем его и показываем
    private fun showTimerEndDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("The end")
            .setMessage("Oops,there is no enough time, try again later.")
            .setCancelable(false)
            .setPositiveButton("Ok"){_,_ ->finish()}
            .create()
        dialog.show()
    }



    private fun createBoxes() {
        val boxBindings = (0 until options.boxCount).map {index ->
            val boxBinding = ItemBoxBinding.inflate(layoutInflater)
            boxBinding.root.id = View.generateViewId()
            boxBinding.boxTitleTextView.text = "Box #${index + 1}"
            //назначаем слушатель нажатия на ящик
            boxBinding.root.setOnClickListener { view -> onBoxSelected(view) }
            boxBinding.root.tag = index
            binding.root.addView(boxBinding.root)
            boxBinding
        }
        //помещаем все ящики во flow,который их размещает
        binding.flow.referencedIds = boxBindings.map { it.root.id }.toIntArray()
    }

    private fun onBoxSelected(view: View) {
        //если пользователь выбрать ящик победитель,то вызываем вьюшку с победой
        //т.е ящик индекс которого равен индексу победного ящика,который задаётся в onCreate
        if(view.tag as Int == boxIndex){
            val intent = Intent(this,BoxActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this,"This box is empty.Try another one.",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_OPTIONS = "EXTRA_OPTIONS"
        private const val KEY_INDEX = "KEY_INDEX"
        private const val KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP"
        private const val KEY_ALREADY_DONE = "KEY_ALREADY_DONE"

        private const val TIMER_DURATION = 10_000L
    }
}