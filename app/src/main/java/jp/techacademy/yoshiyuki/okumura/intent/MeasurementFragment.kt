package jp.techacademy.yoshiyuki.okumura.intent

//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentMeasurementBinding

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
            // FragmentManagerの取得
            // トランザクションの生成・コミット
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, MeasurementFragment())
            ft.commit()
            ft.addToBackStack(null)
        }
            binding.button1.setOnClickListener {
                // WorkerFragmentに戻る
                val ft = parentFragmentManager.beginTransaction()
                ft.replace(R.id.container, WorkerFragment())
                ft.commit()
            }
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
                handler.post(runnable)                // 最初のキュー登録
            }
            // stopボタン押された時の処理
            binding.stop.setOnClickListener {
                handler.removeCallbacks(runnable)      // キューキャンセル
            }
//                // resetボタン押された時の処理
            binding.reset.setOnClickListener {
                handler.removeCallbacks(runnable)      // キューキャンセル
                timeValue = 0                          // 秒カウンタークリア
                timeToText()?.let {                  // timeToText()で表示データを作り
                    binding.timeText.text = it                // timeText.textに表示
                }
            }
//        }

        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


        private fun timeToText(time: Int = 0): String? {
            return if (time < 0) {
                null                                    // 時刻が0未満の場合 null
            } else if (time == 0) {
                "00:00:00"                            // ０なら
            } else {
                val h = time / 3600
                val m = time % 3600 / 60
                val s = time % 60
                "%1$02d:%2$02d:%3$02d".format(h, m, s)  // 表示に整形
            }
        }
    }





