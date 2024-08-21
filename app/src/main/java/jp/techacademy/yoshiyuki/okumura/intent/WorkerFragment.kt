package jp.techacademy.yoshiyuki.okumura.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.techacademy.yoshiyuki.okumura.intent.databinding.FragmentWorkerBinding

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
        binding.button3.setOnClickListener {

        }
        binding.button3.setOnClickListener {
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