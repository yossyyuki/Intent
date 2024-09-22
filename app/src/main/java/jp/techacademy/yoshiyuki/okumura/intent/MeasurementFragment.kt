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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeasurementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ボタンを押したときにCSVエクスポートを実行
        binding.button.setOnClickListener {
            RealmManager.realm?.let { realm ->
                exportRealmDataToCSV(realm)
                Log.d("RealmData", "button pressed at:sssss")
            }
        }

        // ストップウォッチの処理はそのまま（省略可能）
        val handler = Handler()
        var timeValue = 0
        val runnable = object : Runnable {
            override fun run() {
                timeValue++
                timeToText(timeValue)?.let {
                    binding.timeText.text = it
                }
                handler.postDelayed(this, 1000)
            }
        }

        // スタートボタン
        binding.start.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTime))

            Log.d("RealmData", "Start button pressed at: $dateString")

            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            startDate = Date(currentTime).toString() // Date型で保存
                        })
                    }
                    Log.d("RealmData", "データが保存されました: $dateString")
                } catch (e: Exception) {
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }
            handler.post(runnable)
        }

        // ストップボタン
        binding.stop.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(currentTime))

            Log.d("RealmData", "Stop button pressed at: $dateString")

            GlobalScope.launch {
                try {
                    RealmManager.realm?.write {
                        (InputData().apply {
                            stopDate = Date(currentTime).toString() // Date型で保存
                        })
                    }
                    Log.d("RealmData", "データが保存されました: $dateString")
                } catch (e: Exception) {
                    Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                }
            }
            handler.removeCallbacks(runnable)
        }
    }

    // CSVエクスポート関数
    private fun exportRealmDataToCSV(realm: Realm) {
        val dataList = realm.query<InputData>().find()
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val csvFile = File(directory, "realm_data.csv")

        try {
            val fileWriter = FileWriter(csvFile)
            fileWriter.append("ID,OrderNumber,ProcessName,WorkerName,StartDate,EndDate\n")

            dataList.forEach { data ->
                fileWriter.append("${data.id},")
                fileWriter.append("${data.orderNumber},")
                fileWriter.append("${data.processName ?: ""},")
                fileWriter.append("${data.workerName},")
                fileWriter.append("${data.startDate},")
                fileWriter.append("${data.stopDate ?: "null"}\n")
            }

            fileWriter.flush()
            fileWriter.close()

            Log.d("CSV Export", "CSV file successfully exported to: ${csvFile.absolutePath}")
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
