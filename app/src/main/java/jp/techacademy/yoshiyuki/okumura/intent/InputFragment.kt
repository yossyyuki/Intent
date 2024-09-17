package jp.techacademy.yoshiyuki.okumura.intent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentInputBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
        // Bundleからデータを取得
        val inputnumber = arguments?.getInt("TEXT_KEY")
        // 取得したテキストをTextViewに表示
        binding.textView.text = inputnumber.toString()

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
                            RealmManager.realm?.write {
//                                copyToRealmを削除
                                (InputData().apply {
                                    processName = process
                                })
                            }
                            // データが保存されたことをLogcatに出力
                            Log.d("RealmData", "データが保存されました: $process")
                        } catch (e: Exception) {
                            // エラーハンドリング
                            Log.e("RealmData", "データの保存に失敗しました: ${e.message}")
                        }
                    }
                }
            }
        }

        binding.ToWorkerFragment.setOnClickListener {
            // FragmentManagerの取得
            // トランザクションの生成・コミット　WorkerFragmentを表示
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, WorkerFragment())
            ft.commit()
            ft.addToBackStack(null)
            binding.toTopFragment.setOnClickListener {
                // TopFragmentに戻る
                val ft = parentFragmentManager.beginTransaction()
                ft.replace(R.id.container, TopFragment())
                ft.commit()
            }
        }
    }
}


//override fun onDestroyView() {
//    super.onDestroyView()
//    _binding = null






