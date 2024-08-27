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

open class TopFragment : Fragment() {

    private var _binding: FragmentTopBinding? = null
    private val binding: FragmentTopBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        return binding.root
    }


    //    以下の記述でbutton1をクリックすることでInputFragmentを表示させたい。
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button1.setOnClickListener {
            binding.button1.text = "ここをクリックでInputを表示"

        }
        binding.button1.setOnClickListener {
            // FragmentManagerの取得

            // トランザクションの生成・コミット
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, InputFragment())
            ft.commit()
            ft.addToBackStack(null)
        }
//        InputFilterを使用して文字数制限　 数字5桁-2桁
//        -が自動で入るようにしたい
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

//        InputFilterを入れたらonDestroyがエラーになった。なぜ？
//            override fun onDestroyView() {
//                super.onDestroyView()
//                _binding = null
            }
    }





