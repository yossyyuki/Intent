package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentInputBinding

open class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding: FragmentInputBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button2.setOnClickListener {

        }
        binding.button2.setOnClickListener {
            // FragmentManagerの取得
            // トランザクションの生成・コミット
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, WorkerFragment())
            ft.commit()
            ft.addToBackStack(null)
        }
        binding.button5.setOnClickListener {
            // TopFragmentに戻る
            val ft = parentFragmentManager.beginTransaction()
            ft.replace(R.id.container, TopFragment())
            ft.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



