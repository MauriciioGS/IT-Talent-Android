package mx.mauriciogs.ittalent.ui.authentication.signup

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.ui.authentication.SignInException
import mx.mauriciogs.ittalent.ui.authentication.SignUpExceptionHandler
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.safeNavigate
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmentSignUpBinding

@AndroidEntryPoint
class SignUpFragment : BaseFrag<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private lateinit var mBinding: FragmentSignUpBinding

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun FragmentSignUpBinding.initialize() {
        mBinding = this
        saveUserType()
        initObservers()
        initUI()
    }

    private fun saveUserType() {
        val user = arguments?.getBoolean(USER_KEY)
        if (user != null) signUpViewModel.setUserType(user)
    }

    private fun initObservers() {
        signUpViewModel.stopButtonContinue()
        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) {
            signUpUi(it?: return@observe)
        }
    }

    private fun signUpUi(signUpUIModel: SignUpUIModel) {
        if (signUpUIModel.enableNextStep) {
            findNavControllerSafely()?.safeNavigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpFragment2())
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
