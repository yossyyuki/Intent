package jp.techacademy.yoshiyuki.okumura.intent

//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentMeasurementBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

open class MeasurementFragment : Fragment() {

    private var _binding: FragmentMeasurementBinding? = null
    private val binding: FragmentMeasurementBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeasurementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {

//            次に渡す画面がないのでコメントアウト
            // FragmentManagerの取得
            // トランザクションの生成・コミット
//            val ft = parentFragmentManager.beginTransaction()
//            ft.replace(R.id.container, MeasurementFragment())
//            ft.commit()
//            ft.addToBackStack(null)
        }
//        「戻る」を動作を制限するためにコメントアウト
//        binding.ToWorkerFragment.setOnClickListener {
//            // WorkerFragmentに戻る
//            val ft = parentFragmentManager.beginTransaction()
//            ft.replace(R.id.container, WorkerFragment())
//            ft.commit()
//        }
        //ストップウォッチ用のコード

        val handler = Handler()                      //
        var timeValue = 0                              // 秒カウンター

        //

//        binding.start.setOnClickListener {

//                // Handler(スレット間通信：イベントキュー？)
        val runnable = object : Runnable {
            //                    // メッセージ受信が有った時かな?
            override fun run() {
                timeValue++                      // 秒カウンタ+1
                timeToText(timeValue)?.let {        // timeToText()で表示データを作り
                    binding.timeText.text = it           // timeText.textへ代入(表示)
                }
                handler.postDelayed(this, 1000)  // 1000ｍｓ後に自分にpost
            }
        }
//
//                // startボタン押された時(setOnClickListener)の処理
        binding.start.setOnClickListener {
            val currentTime = System.currentTimeMillis() // 現在の時間（ミリ秒）を取得
            val dateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // フォーマットを指定
            val dateString = dateFormat.format(Date(currentTime)) // 現在の時間をフォーマット

            // Logcat に記録
            Log.d("RealmData", "Start button pressed at: $dateString")

            // Realmへの保存処理
            val startdate = dateString

            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            startDate = startdate
                        })
                    }
                    // データが保存されたことをLogcatに出力
                    Log.d("RealmData", "データが保存されました: $startdate")
                } catch (e: Exception) {
                    // エラーハンドリング
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }



            handler.post(runnable)                // 最初のキュー登録
        }
        // stopボタン押された時の処理
        binding.stop.setOnClickListener {
            val currentTime = System.currentTimeMillis() // 現在の時間（ミリ秒）を取得
            val dateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // フォーマットを指定
            val dateString = dateFormat.format(Date(currentTime)) // 現在の時間をフォーマット

            // Logcat に記録
            Log.d("RealmData", "Stop button pressed at: $dateString")

            // Realmへの保存処理
            val stopdate = dateString
            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            startDate = stopdate
                        })
                    }
                    // データが保存されたことをLogcatに出力
                    Log.d("RealmData", "データが保存されました: $stopdate")
                } catch (e: Exception) {
                    // エラーハンドリング
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }



            handler.removeCallbacks(runnable)      // キューキャンセル
        }
//                // resetボタン押された時の処理
//        binding.reset.setOnClickListener {
//            handler.removeCallbacks(runnable)      // キューキャンセル
//            timeValue = 0                          // 秒カウンタークリア
//            timeToText()?.let {                  // timeToText()で表示データを作り
//                binding.timeText.text = it                // timeText.textに表示
//            }
//        }
//        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun timeToText(time: Int = 0): String? {
        if (time < 0) {
            return null                                    // 時刻が0未満の場合 null
        } else if (time == 0) {
            return "00:00:00"                            // ０なら
        } else {
            val h = time / 3600
            val m = time % 3600 / 60
            val s = time % 60
            return "%1$02d:%2$02d:%3$02d".format(h, m, s)  // 表示に整形
        }
    }
}





