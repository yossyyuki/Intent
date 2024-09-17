package jp.techacademy.yoshiyuki.okumura.intent


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.techacademy.yoshiyuki.okumura.intent.databinding.ActivityMainBinding


open class MainActivity : AppCompatActivity() {

    /**
     * クラス変数
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * クラス関数
     * - overrideしてるもの
     * - non-private
     * - private
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setUpManager()

//        binding.textView.text = "作業時間を計測します"

//        Activity上にTopFragmentを表示
        val fragment = TopFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        RealmManager.release()
    }

    /**
     * 各種マネージャーのセットアップ
     */
    private fun setUpManager() {
        RealmManager.setUp()
    }
}
