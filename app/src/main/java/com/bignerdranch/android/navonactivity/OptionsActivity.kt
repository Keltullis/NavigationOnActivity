package com.bignerdranch.android.navonactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bignerdranch.android.navonactivity.databinding.ActivityOptionsBinding
import com.bignerdranch.android.navonactivity.model.Options

class OptionsActivity : BaseActivity() {

    private lateinit var binding:ActivityOptionsBinding

    private lateinit var options:Options

    private lateinit var boxCountItems:List<BoxCountItem>

    private lateinit var adapter:ArrayAdapter<BoxCountItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //читаем опции,либо из сохранённого состояния(вдруг экран повернули)
        //или же из интента,если мы зашли на экран впервые
        //ну и если мы накосячили и не положили параметры то выпадет ошибка
        //getТотТипКоторыйПоложили
        options = savedInstanceState?.getParcelable<Options>(KEY_OPTIONS)?:
                intent.getParcelableExtra(EXTRA_OPTIONS)?:
                throw IllegalStateException("You need to specify EXTRA_OPTIONS argument to launch this activity")

        setupSpinner()
        setupCheckBox()
        updateUi()

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            onConfirmPressed()
        }
    }

    private fun onConfirmPressed() {
        val intent = Intent()
        intent.putExtra(EXTRA_OPTIONS,options)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    private fun updateUi() {
        binding.enableTimerCheckBox.isChecked = options.isTimerEnabled

        val currentIndex = boxCountItems.indexOfFirst { it.count==options.boxCount }
        binding.boxCountSpinner.setSelection(currentIndex)
    }

    private fun setupCheckBox() {
        binding.enableTimerCheckBox.setOnClickListener {
            options = options.copy(isTimerEnabled = binding.enableTimerCheckBox.isChecked)
        }
    }

    private fun setupSpinner() {
        //используем Plurals,1-id ресурса,2-число для установки правильного склонения,3-число для вставки вместо %d
        boxCountItems = (1..6).map {
            BoxCountItem(it,resources.getQuantityString(R.plurals.boxes,it,it))
        }
        //заполняем список
        adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            boxCountItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        binding.boxCountSpinner.adapter = adapter
        binding.boxCountSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val count = boxCountItems[position].count
                options = options.copy(boxCount = count)
            }
        }
    }

    companion object{
        const val EXTRA_OPTIONS = "EXTRA_OPTIONS"
        private const val KEY_OPTIONS = "KEY_OPTIONS"
    }


    //кол-во ящиков и текст для пользователя
    class BoxCountItem(
        val count: Int,
        private val optionTitle: String
    ) {
        override fun toString(): String {
            return optionTitle
        }
    }
}