package jp.techacademy.yoshiyuki.okumura.intent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import io.realm.kotlin.ext.query
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentWorkerBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class WorkerFragment : Fragment() {

    private var _binding: FragmentWorkerBinding? = null
    private val binding: FragmentWorkerBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bundleからidデータを取得　?:0でエラー防ぐ
        val ID = arguments?.getInt("id") ?: 0



        // Realmからidに紐づいたprocessNameを取得し表示する
        GlobalScope.launch {
            // Realmからデータを取得（例として id=1 のデータを取得）
//            TODO:processViewにprocessNameが表示されない問題
            val inputData =
                RealmManager.realm?.query<InputData>("id == $ID")?.first()?.find()
            inputData?.let {
                activity?.runOnUiThread {
                    binding.processView.text = it.processName.toString()// データを表示

                    // データの保存　chipをクリックで着火
//            TODO:setOnCheckedStateChangeListenerが動いていない。37行目とかにいれると動くので場所が悪い？
                    binding.chipGroup1.setOnCheckedStateChangeListener { group, checkedIds ->
                        println(" RealmData ==== *** click")
                        // チップが選択されているか確認
                        if (checkedIds.isNotEmpty()) {
                            // チェックされたチップIDを取得
                            val checkedChipId = checkedIds[0] // singleSelectionなので、最初の1つだけ取得
                            // 選択されたチップを取得
                            val selectedChip = group.findViewById<Chip>(checkedChipId)
                            // Realmに保存する
                            selectedChip?.let {
                                val worker = it.text
                                // Realmへの保存処理
                                GlobalScope.launch {
                                    try {
                                        // Realmの書き込みトランザクションを開始
                                        RealmManager.realm?.write {
                                            // プライマリーキーのidを使って書き込み対象レコード取得
                                            val inputDataRecord =
                                                query<InputData>("id == $0", ID).first()
                                                    .find()

                                            inputDataRecord.also { data ->
                                                // データが存在する場合は、processNameを上書きする
                                                if (data != null) {
                                                    data.workerName = worker as String
                                                }
                                            }
                                            // データが保存されたことをLogcatに出力
                                            Log.d("RealmData", "データが保存されました: $worker")
                                            Log.d(
                                                "RealmData",
                                                "Realmデータ確認: id（$ID）のworkerNameは、${inputDataRecord?.workerName} です"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        // エラーハンドリング
                                        Log.e(
                                            "RealmData",
                                            "データの保存に失敗しました: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


// InputFragmentに戻る
        binding.ToInputFragment.setOnClickListener {
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, InputFragment())
            ft.commit()
        }
// MeasurementFragmentを表示
        binding.nextToMeasurementFragment.setOnClickListener {

            // FragmentManagerの取得
            // トランザクションの生成・コミット
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, MeasurementFragment())
            ft.commit()
            ft.addToBackStack(null)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}










