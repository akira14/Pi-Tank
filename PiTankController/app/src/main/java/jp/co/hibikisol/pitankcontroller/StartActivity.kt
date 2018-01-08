package jp.co.hibikisol.pitankcontroller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        button3.setOnClickListener {
            val intent = Intent (this, MainActivity::class.java)
            intent.putExtra("TANK-IP", tankIP.text)
            startActivity(intent)
        }

    }

}
