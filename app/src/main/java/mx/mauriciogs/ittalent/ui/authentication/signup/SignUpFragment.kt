package mx.mauriciogs.ittalent.ui.authentication.signup

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentSignUpBinding
import mx.mauriciogs.ittalent.ui.authentication.SignInException
import mx.mauriciogs.ittalent.ui.authentication.SignUpExceptionHandler
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.TALENT_UT
import mx.mauriciogs.ittalent.ui.global.extensions.showError
import mx.mauriciogs.ittalent.ui.global.extensions.snackbar

class SignUpFragment : BaseFrag<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private lateinit var mBinding: FragmentSignUpBinding

    private val signUpViewModel: SignUpViewModel by viewModels(){ SignUpViewModel.SignUpFragmentsVMFactory() }

    override fun FragmentSignUpBinding.initialize() {
        mBinding = this
        saveUserType()
        initObservers()
        initUI()
    }

    private fun saveUserType() {
        val user = arguments?.getBoolean(USER_KEY)
        if (user != null) signUpViewModel.setUser(user)
    }

    private fun initObservers() {
        signUpViewModel.setUserType(Int.TALENT_UT())
        signUpViewModel.stopButtonContinue()
        signUpViewModel.signUpUIModel.observe(requireActivity()) {
            signUpUi(it?: return@observe)
        }
    }

    private fun signUpUi(signUpUIModel: SignUpUIModel) {
        if (signUpUIModel.enableNextStep) {
            signUpViewModel.stopButtonContinue()
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpFragment2())
        }
    }

    private fun initUI() {
        with(mBinding) {
            btnContinue.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val (invalidEmail, exception) = SignUpExceptionHandler().invalidEmail(email)
                if (invalidEmail) showError(exception)
                else signUpViewModel.setUserEmail(email)
            }
        }
    }

    private fun showError(exception: Exception) = exception.run {
        when(this) {
            is SignInException.Email -> snackbar(error).showError()
            else -> snackbar(message).showError()
        }
    }

    companion object {

        const val USER_KEY = "user"
    }
}
