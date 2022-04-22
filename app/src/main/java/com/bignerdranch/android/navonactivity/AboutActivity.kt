package com.bignerdranch.android.navonactivity


import android.os.Bundle

import com.bignerdranch.android.navonactivity.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    private lateinit var binding:ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.versionNameTextView.text = BuildConfig.VERSION_NAME
        binding.versionCodeTextView.text = BuildConfig.VERSION_CODE.toString()
        binding.okButton.setOnClickListener {
            finish()
        }
    }
}