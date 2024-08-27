package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import jp.techacademy.yoshiyuki.okumura.intent.databinding.ActivityMainBinding

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

//        setContentView(R.layout.fragment_top)は不要なのでコメントアウト
//        setContentView(R.layout.fragment_top)
        setContentView(binding.root)

//
//        binding.textView.text = "作業時間を計測します"

//        Activity上にTopFragmentを表示
        val fragment = TopFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container, fragment)
        fragmentTransaction.commit()

    }
}

