package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_description_tv.text = intent.getStringExtra(getString(R.string.file_name_extra)).toString()
        status_tv.text = intent.getStringExtra(getString(R.string.status_extra)).toString()

        fab.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

}
