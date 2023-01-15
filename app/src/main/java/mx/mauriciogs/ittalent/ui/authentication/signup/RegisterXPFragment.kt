package mx.mauriciogs.ittalent.ui.authentication.signup

import androidx.fragment.app.activityViewModels
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterXPBinding
import mx.mauriciogs.ittalent.ui.global.BaseFrag

class RegisterXPFragment : BaseFrag<FragmentRegisterXPBinding>(R.layout.fragment_register_x_p) {
    private lateinit var mBinding: FragmentRegisterXPBinding

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun FragmentRegisterXPBinding.initialize() {
        mBinding = this
        initObservers()
        initUI()
    }

    private fun initObservers() {
        signUpViewModel.stopButtonContinue()

    }
    private fun initUI() {
        with(mBinding) {

            val modalidades = resources.getStringArray(R.array.modal)
            dropdownMenuModalidad.setText(modalidades[0], false)
            val tipos = resources.getStringArray(R.array.type_job)
            dropdownMenuTipo.setText(tipos[0], false)
        }
    }

}