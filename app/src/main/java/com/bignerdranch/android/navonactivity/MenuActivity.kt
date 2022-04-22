package com.bignerdranch.android.navonactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bignerdranch.android.navonactivity.databinding.ActivityMenuBinding
import com.bignerdranch.android.navonactivity.model.Options

class MenuActivity :BaseActivity() {

    private lateinit var binding:ActivityMenuBinding

    private lateinit var options:Options

    //Первый способ
    //private var resultLauncher:ActivityResultLauncher<Intent>? = null

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result:ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent:Intent? = result.data
            options = intent?.getParcelableExtra(OptionsActivity.EXTRA_OPTIONS)?:throw IllegalStateException("Can't get the updated data from options activity")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openBoxButton.setOnClickListener {
            onOpenBoxPressed()
        }
        binding.optionsButton.setOnClickListener {
            onOptionsPressed()
        }
        binding.aboutButton.setOnClickListener {
            onAboutPressed()
        }
        binding.exitButton.setOnClickListener {
            onExitPressed()
        }

        options = savedInstanceState?.getParcelable(KEY_OPTIONS) ?:Options.DEFAULT


        //startActivityForResult() устарела,теперь используется новый метод
        //в нём проверяется код результата,затем получают интент по коду который отправляли при запуске
        //повторю,код активити не проверяется,проверяется только код extra
        //в остальном всё по старому в новом активити запускаем setResult
        //на requestCode проверять не нужно,потому что результат придёт только с запущенного активити
        //что бы запустить несколько активити,нужно несколько коллбэков
        //Второй способ
        //resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result:ActivityResult ->
        //        if(result.resultCode == Activity.RESULT_OK){
        //            val intent:Intent? = result.data
        //            options = intent?.getParcelableExtra(OptionsActivity.EXTRA_OPTIONS)?:throw IllegalStateException("Can't get the updated data from options activity")
        //        }
        //    }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_OPTIONS,options)
    }


    private fun onOpenBoxPressed() {
        val intent = Intent(this,BoxSelectionActivity::class.java)
        intent.putExtra(OptionsActivity.EXTRA_OPTIONS,options)
        startActivity(intent)
    }

    private fun onOptionsPressed() {
        val intent = Intent(this,OptionsActivity::class.java)
        //кладём в интент ключ и парселэйбл данные
        intent.putExtra(OptionsActivity.EXTRA_OPTIONS,options)
        //resultLauncher?.launch(intent)
        resultLauncher.launch(intent)
    }

    private fun onAboutPressed() {
        val intent = Intent(this,AboutActivity::class.java)
        startActivity(intent)
    }

    private fun onExitPressed() {
        finish()
    }

    companion object{
        private const val KEY_OPTIONS = "OPTIONS"
        //private const val OPTIONS_REQUEST_CODE = 1
    }
}

