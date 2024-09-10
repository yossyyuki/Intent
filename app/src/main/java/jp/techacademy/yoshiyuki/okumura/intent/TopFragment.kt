package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentInputBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.text.InputFilter
import java.util.regex.Pattern
import android.text.Spanned
import android.util.Log
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentTopBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmScalarNullableQuery
import io.realm.kotlin.query.max
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.*

open class TopFragment : Fragment() {

    //    private lateinit var realm: Realm
    private var _binding: FragmentTopBinding? = null
    private val binding: FragmentTopBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Realmの設定
//        val config = RealmConfiguration.Builder(schema = setOf(InputData::class))
//            .name("myrealm.realm") // データベースファイル名を指定
//            .build()
//        realm = Realm.open(config)

        // データの保存　
        binding.ToInputFragment.setOnClickListener {
            val inputnumber = binding.editText.text.toString()

            GlobalScope.launch {
                RealmManager.realm?.write {
                    try {
                        /*<Long>を<Int>に変更*/
                        val maxId: RealmScalarNullableQuery<Int> = query<InputData>().max<Int>("id")
                        val nextId = (maxId.find() ?: 0) + 1
                        copyToRealm(InputData().apply {
                            id = nextId.toInt()
                            orderNumber = inputnumber.toInt()
                        })
                        // Bundleでデータを渡す
                        val bundle = Bundle().apply {
                            putInt("TEXT_KEY", inputnumber.toInt())
                        }
                        //InputFragmentにBundleをセット
                        //81行目で使用していたInputFragmentインスタンスをinputFragmentという変数にする
                        val inputFragment = InputFragment().apply {
                            arguments = bundle
                        }

                        val orderNumber =
                            query<InputData>("id==${nextId}").find().first().orderNumber
                        Log.d("RealmData", "Data saved with id: $nextId")
                        Log.d("RealmData", "Data saved with 受注番号: $orderNumber")

                        //InputFragmentに遷移し、BackStackを有効にする
                        val ft = parentFragmentManager.beginTransaction()
                        //inputFragmentをreplaceに渡すことでargumentsも含まれる。
                        ft.replace(R.id.container, inputFragment)
                        ft.commit()
                        ft.addToBackStack(null)

                    } catch (e: Exception) {
                        Log.e("RealmData", "Error saving data: ${e.message}")
                    }
                }

            }

        }


        // InputFilterを使用して文字数制限  数字5桁-2桁
//        val filter = object : InputFilter {
//            val pattern: Pattern = Pattern.compile("^[0-9]{0,5}-?[0-9]{0,2}$")
//            override fun filter(
//                source: CharSequence,
//                start: Int,
//                end: Int,
//                dest: Spanned,
//                dstart: Int,
//                dend: Int
//            ): CharSequence? {
//                val result = dest.toString().substring(0, dstart) +
//                        source.toString().substring(start, end) +
//                        dest.toString().substring(dend)
//                val matcher = pattern.matcher(result)
//                return if (matcher.matches()) null else ""
//            }
//        }
//        binding.editText.filters = arrayOf(filter)
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        // Fragmentのビューが破棄されたときにRealmインスタンスを閉じる
//        RealmManager.realm?.close()
        _binding = null // ビューの解放
    }
}
