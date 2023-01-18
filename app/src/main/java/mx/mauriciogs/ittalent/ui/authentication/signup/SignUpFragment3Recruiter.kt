package mx.mauriciogs.ittalent.ui.authentication.signup

import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterProfileBinding
import mx.mauriciogs.ittalent.ui.global.BaseFrag

class SignUpFragment3Recruiter : BaseFrag<FragmentRegisterProfileBinding>(R.layout.fragment_register_profile) {

    private lateinit var mBinding: FragmentRegisterProfileBinding

    override fun FragmentRegisterProfileBinding.initialize() {
        mBinding = this
    }
}