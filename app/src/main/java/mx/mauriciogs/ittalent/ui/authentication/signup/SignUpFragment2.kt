package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.ui.authentication.SignInException
import mx.mauriciogs.ittalent.ui.authentication.SignUpExeption
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.databinding.FragmentSignUp2Binding

@AndroidEntryPoint
class SignUpFragment2 : BaseFrag<FragmentSignUp2Binding>(R.layout.fragment_sign_up2) {

    private lateinit var mBinding: FragmentSignUp2Binding

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var isTalent = Boolean.yes()

    override fun FragmentSignUp2Binding.initialize() {
        mBinding = this
        signUpViewModel.getUser()
        initObservers()
    }

    private fun initObservers() {
        signUpViewModel.stopButtonContinue()

        signUpViewModel.isTalent.observe(viewLifecycleOwner) {
            isTalent = it
            initUI()
        }
        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) {
            signUpUi(it?: return@observe)
        }
    }

    private fun initUI() {
        with(mBinding) {
            if (isTalent) tvWelcome.text = getString(R.string.welcome_talent)
            else tvWelcome.text = getString(R.string.welcome_recruiter)

            btnContinue.setOnClickListener {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

                checkCredentials()
            }
        }
    }

    private fun checkCredentials() {
        with(mBinding) {
            val fullName = etName.text.toString()
            val password = etPass1.text.toString().trim()
            val passConfirm = etPass2.text.toString().trim()
            signUpViewModel.saveNamePass(password, passConfirm, fullName)
        }
    }

    private fun signUpUi(signUpUIModel: SignUpUIModel) = signUpUIModel.run{
        if (enableNextStep) {
            if (isTalent) findNavControllerSafely()?.safeNavigate(SignUpFragment2Directions.actionSignUpFragment2ToSignUpFragment3())
            else findNavControllerSafely()?.safeNavigate(SignUpFragment2Directions.actionSignUpFragment2ToFragmentRegisterProfile())
        }
        if (exception != null) showError(exception)
    }

    private fun showError(exception: Exception) = exception.run {
        when(this) {
            is SignUpExeption.Name -> snackbar(error).showError()
            is SignInException.Password -> snackbar(error).showError()
            is SignUpExeption.Passwords -> snackbar(error).showError()
            else -> snackbar(message).showError()
        }
    }

}
