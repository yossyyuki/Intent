package jp.techacademy.yoshiyuki.okumura.intent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import io.realm.kotlin.ext.query
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentInputBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.realm.kotlin.Realm


open class InputFragment : Fragment() {
    //    private lateinit var realm: Realm
    private var _binding: FragmentInputBinding? = null
    private val binding: FragmentInputBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bundleからidデータを取得　?:0でエラー防ぐ oedernumberがわかりにくいのでIDに変更した
//
        val ID = arguments?.getInt("id") ?: 0

//        Realmからidに紐づいたorderNumberを取得し表示する
        GlobalScope.launch {
            val inputData =
                RealmManager.realm?.query<InputData>("id == $ID")?.first()?.find()
            inputData?.let {
                activity?.runOnUiThread {
                    binding.orderNumberView.text = it.orderNumber.toString() // データを表示


                    // データの保存　chipをクリックで着火
                    binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                        println(" RealmData ==== *** click")
                        // チップが選択されているか確認
                        if (checkedIds.isNotEmpty()) {
                            // チェックされたチップIDを取得
                            val checkedChipId = checkedIds[0] // singleSelectionなので、最初の1つだけ取得
                            // 選択されたチップを取得
                            val selectedChip = group.findViewById<Chip>(checkedChipId)
                            // Realmに保存する
                            selectedChip?.let {
                                val process = it.text.toString()
                                // Realmへの保存処理
                                GlobalScope.launch {
                                    try {
                                        // Realmの書き込みトランザクションを開始
                                        RealmManager.realm?.write {
                                            // プライマリーキーのidを使って書き込み対象レコード取得
                                            val inputDataRecord =
                                                query<InputData>("id == $0", ID).first()
                                                    .find()
//                                            ↑idがordernumberと一致する最初のInputDataを取得

                                            inputDataRecord.also { data ->
                                                // データが存在する場合は、processNameを上書きする
                                                if (data != null) {
                                                    data.processName = process
                                                }

                                            }

                                            // データが保存されたことをLogcatに出力
                                            Log.d("RealmData", "データが保存されました: $process")
                                            Log.d(
                                                "RealmData",
                                                "Realmデータ確認: id（$ID）のprocessNameは、${inputDataRecord?.processName} です"
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

        // TopFragmentに戻る
        binding.toTopFragment.setOnClickListener {
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, TopFragment())
            ft.commit()

        }


        // WorkerFragmentを表示
        binding.ToWorkerFragment.setOnClickListener {
            // 渡すデータをBundleにセット
            val bundle = Bundle().apply {
                putInt("id", ID) // IDはデータベースのプライマリーキーのidの値。それをidというキーに保存
            }
            // WorkerFragmentを作成し、Bundleを渡す
            val workerFragment = WorkerFragment().apply {
                arguments = bundle
            }

            // FragmentManagerの取得
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, workerFragment)
            ft.commit()
            ft.addToBackStack(null)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

