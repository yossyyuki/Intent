package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
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
                    val worker = it.text.toString()

                    // Realmへの保存処理
                    GlobalScope.launch {
                        try {
                            RealmManager.realm?.write {
//                                copyToRealmを削除
                                (InputData().apply {
                                    workerName = worker
                                })
                            }
                            // データが保存されたことをLogcatに出力
                            Log.d("RealmData", "データが保存されました: $worker")
                        } catch (e: Exception) {
                            // エラーハンドリング
                            Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                        }
                    }

                    binding.nextToMeasurementFragment.setOnClickListener {
                        // FragmentManagerの取得
                        // トランザクションの生成・コミット
                        val ft = parentFragmentManager.beginTransaction()
                        ft.replace(R.id.container, MeasurementFragment())
                        ft.commit()
                        ft.addToBackStack(null)
                    }
                    binding.ToInputFragment.setOnClickListener {
                        // InputFragmentに戻る
                        val ft = parentFragmentManager.beginTransaction()
                        ft.replace(R.id.container, InputFragment())
                        ft.commit()
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
