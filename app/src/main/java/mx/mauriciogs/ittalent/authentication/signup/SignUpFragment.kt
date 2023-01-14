package mx.mauriciogs.ittalent.authentication.signup

import androidx.navigation.fragment.findNavController
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentSignUpBinding
import mx.mauriciogs.ittalent.global.BaseFrag

class SignUpFragment : BaseFrag<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private lateinit var mBinding: FragmentSignUpBinding

    override fun FragmentSignUpBinding.initialize() {
        mBinding = this
        initListeners()
    }

    private fun initListeners() {
        with(mBinding) {
            btnContinue.setOnClickListener {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpFragment2())
            }
        }
    }
}
