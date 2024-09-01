package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.text.InputFilter
import java.util.regex.Pattern
import android.text.Spanned
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentTopBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmScalarNullableQuery
import io.realm.kotlin.query.max
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class TopFragment : Fragment() {

    private lateinit var realm: Realm
    private var _binding: FragmentTopBinding? = null
    private val binding: FragmentTopBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Realmの設定
        val config = RealmConfiguration.Builder(schema = setOf(InputData::class))
            .name("myrealm.realm")
            .build()
        realm = Realm.open(config)

        // データの保存
        binding.editText.setOnClickListener {
            val inputText = binding.editText.text.toString()

            GlobalScope.launch {
                realm.write {
                    val maxId: RealmScalarNullableQuery<Long> = realm.query<InputData>().max<Long>("id")
                    val nextId = (maxId ?: 0) + 1
                    val data = copyToRealm(InputData().apply {
                        id = nextId
                        content = inputText
                    })
                }
            }
        }

        // データの読み込み
        GlobalScope.launch {
            val savedData = realm.query<InputData>().find()
            savedData.lastOrNull()?.let { data ->
                activity?.runOnUiThread {
                    binding.editText.setText(data.content)
                }
            }
        }

        // Fragmentの切り替え
        binding.ToInputFragment.setOnClickListener {
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, InputFragment())
            ft.commit()
            ft.addToBackStack(null)
        }

        // InputFilterを使用して文字数制限  数字5桁-2桁
        val filter = object : InputFilter {
            val pattern: Pattern = Pattern.compile("^[0-9]{0,5}-?[0-9]{0,2}$")
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val result = dest.toString().substring(0, dstart) +
                        source.toString().substring(start, end) +
                        dest.toString().substring(dend)
                val matcher = pattern.matcher(result)
                return if (matcher.matches()) null else ""
            }
        }
        binding.editText.filters = arrayOf(filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragmentのビューが破棄されたときにRealmインスタンスを閉じる
        realm.close()
        _binding = null // ビューの解放
    }
}

