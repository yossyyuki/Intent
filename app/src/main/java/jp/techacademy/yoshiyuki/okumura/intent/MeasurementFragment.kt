package jp.techacademy.yoshiyuki.okumura.intent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentMeasurementBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Locale

open class MeasurementFragment : Fragment() {

    private var _binding: FragmentMeasurementBinding? = null
    private val binding: FragmentMeasurementBinding get() = _binding!!

    private var timeValue = 0 // timeValueはタイマーの時間を保持する変数
    private var job: Job? = null // CoroutineのJobを保持　コルーチンは非同期に処理が可能

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeasurementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CSVエクスポートを実行
        binding.sendresultbutton.setOnClickListener {
            RealmManager.realm?.let { realm ->
                exportRealmDataToCSV(realm)
                Log.d("RealmData", "button pressed at:success")
            }
        }

        // スタートボタンクリック時の日時取得
        binding.start.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTime))

            Log.d("RealmData", "Start button pressed at: $dateString")

            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            startDate = Date(currentTime).toString() // Date型をStringに変換して保存
                        })
                    }
                    Log.d("RealmData", "データが保存されました: $dateString")
                } catch (e: Exception) {
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }
            binding.start.isEnabled = false // 一度Startボタンを押したらStart機能を無効にする
            //TODO:入力を間違えた際に戻れないのがネック。対応必要かも


            // Coroutineでタイマー開始
            job = GlobalScope.launch(Dispatchers.Main) {
                while (isActive) {
//                    タイマーの経過時間を管理　1秒ごとにtimeValueが1増える
                    timeValue++
//                    timeValueの時間を時：分：秒の形式に変換し、timeTextに表示する
                    timeToText(timeValue)?.let {
                        binding.timeText.text = it
                    }
                    delay(1000) // 1秒ごとに更新
                }
            }
        }

        // ストップボタンクリック時の日時取得
        binding.stop.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTime))

            Log.d("RealmData", "Stop button pressed at: $dateString")

            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            stopDate = Date(currentTime).toString() // Date型をStringに変換して保存
                        })
                    }
                    Log.d("RealmData", "データが保存されました: $dateString")
                } catch (e: Exception) {
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }
            binding.stop.isClickable = false // 一度stopボタンを押したらStop機能を無効にする
            //TODO:入力を間違えた際に戻れないのがネック。対応必要かも。別の方法を検討。


            // Coroutineを停止
            job?.cancel()
        }
    }

    // CSVをエクスポートする関数
    private fun exportRealmDataToCSV(realm: Realm) {
//        InputDataに入ってる全てのデータを対象にする
        val dataList = realm.query<InputData>().find()
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val csvFile = File(directory, "realm_data.csv")

        try {
            val fileWriter = FileWriter(csvFile)
//            CSVの列のタイトルを記入する。ヘッダー行
            fileWriter.append("ID,OrderNumber,ProcessName,WorkerName,StartDate,StopDate\n")
//            \nで行ごとにデータを区切る

            dataList.forEach { data ->
//                格データ自体をCSVに書き込んでいく
                fileWriter.append("${data.id},")
                fileWriter.append("${data.orderNumber},")
                fileWriter.append("${data.processName ?: ""},")
                fileWriter.append("${data.workerName},")
                fileWriter.append("${data.startDate},")
                fileWriter.append("${data.stopDate ?: "null"}\n")
            }

//            バッファにあるデータを強制的に書き込む
            fileWriter.flush()
//            ファイルを閉じる
            fileWriter.close()

            Log.d("CSV Export", "CSV file successfully exported to: ${csvFile.absolutePath}")
//            IOException=入出力処理中の例外を管理するクラス
        } catch (e: IOException) {
            Log.e("CSV Export", "Error while writing to CSV", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun timeToText(time: Int = 0): String? {
        if (time < 0) return null
        if (time == 0) return "00:00:00"
        val h = time / 3600
        val m = time % 3600 / 60
        val s = time % 60
        return "%1$02d:%2$02d:%3$02d".format(h, m, s)
    }
}
