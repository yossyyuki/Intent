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
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

open class MeasurementFragment : Fragment() {

    private var _binding: FragmentMeasurementBinding? = null
    private val binding: FragmentMeasurementBinding get() = _binding!!

    private var timeValue = 0 // timeValueはタイマーの時間を保持する変数
    private var job: Job? = null // CoroutineのJobを保持

    private lateinit var driveService: Drive

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
                val csvFile = exportRealmDataToCSV(realm) // CSVファイルをエクスポートして、ファイルオブジェクトを取得
                if (csvFile != null) {
                    uploadFileToGoogleDrive(csvFile) // CSVファイルが存在する場合にGoogle Driveにアップロード
                } else {
                    Log.e("CSV Export", "CSV file creation failed.")
                }
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

            // Coroutineでタイマー開始
            job = GlobalScope.launch(Dispatchers.Main) {
                while (isActive) {
                    timeValue++
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

            // Coroutineを停止
            job?.cancel()
        }
    }

    // Google Driveにファイルをアップロードする関数
    private fun uploadFileToGoogleDrive(csvFile: File) {
        // OAuth 2.0 認証の設定
        val googleCredentialsJson =
            requireContext().assets.open("client_secret_537547056657-4dftgq51l3dhqe3o42v4s464m7rp5t2b.apps.googleusercontent.com.json")
        val clientSecrets = GoogleClientSecrets.load(
            GsonFactory.getDefaultInstance(),
            InputStreamReader(googleCredentialsJson)
        )

        val flow = GoogleAuthorizationCodeFlow.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            clientSecrets,
            Collections.singleton(DriveScopes.DRIVE_FILE)
        ).setAccessType("offline").build()

        val credential: Credential = flow.loadCredential("user")

        // Drive APIのインスタンスを作成
        driveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Drive API Kotlin App").build()

        // ファイルメタデータの作成
        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = csvFile.name

        // ファイルのアップロード
        val mediaContent = FileContent("text/csv", csvFile)
        val uploadedFile = driveService.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()

        Log.d("Drive API", "File uploaded successfully: ${uploadedFile.id}")
    }

    // 修正済みのCSVエクスポート関数
    private fun exportRealmDataToCSV(realm: Realm): File? { // Fileを返すように修正
        val directory =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) // アプリ専用の外部ストレージ
        val csvFile = File(directory, "realm_data.csv")

        return try {
            val fileWriter = FileWriter(csvFile)
            fileWriter.append("ID,OrderNumber,ProcessName,WorkerName,StartDate,StopDate\n")
            realm.query<InputData>().find().forEach { data ->
                fileWriter.append("${data.id},${data.orderNumber},${data.processName ?: ""},${data.workerName},${data.startDate},${data.stopDate ?: "null"}\n")
            }
            fileWriter.flush()
            fileWriter.close()
            Log.d("CSV Export", "CSV file successfully exported to: ${csvFile.absolutePath}")
            csvFile // 成功した場合はcsvFileを返す
        } catch (e: IOException) {
            Log.e("CSV Export", "Error while writing to CSV", e)
            null // 失敗した場合はnullを返す
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
