package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import jp.techacademy.yoshiyuki.okumura.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}