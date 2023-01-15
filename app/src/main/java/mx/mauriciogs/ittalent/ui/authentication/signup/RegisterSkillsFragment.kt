package mx.mauriciogs.ittalent.ui.authentication.signup


import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterSkillsBinding
import mx.mauriciogs.ittalent.ui.global.BaseFrag

class RegisterSkillsFragment : BaseFrag<FragmentRegisterSkillsBinding>(R.layout.fragment_register_skills) {

    private lateinit var mBinding: FragmentRegisterSkillsBinding

    override fun FragmentRegisterSkillsBinding.initialize() {
        mBinding = this
    }


}
