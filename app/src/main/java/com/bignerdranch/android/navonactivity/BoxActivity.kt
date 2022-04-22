package com.bignerdranch.android.navonactivity


import android.content.Intent
import android.os.Bundle
import com.bignerdranch.android.navonactivity.databinding.ActivityBoxBinding

class BoxActivity : BaseActivity(){

    private lateinit var binding:ActivityBoxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toMainMenuButton.setOnClickListener {
            val intent = Intent(this,MenuActivity::class.java)
            //флаг означает что меню активити очистит стэк активити и запустит меню
            //а если она уже есть,то она почистит стэк и оставит только меню без пересоздания
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}