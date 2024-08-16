package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            ft.replace(R.id.container,InputFragment())
            ft.commit()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





