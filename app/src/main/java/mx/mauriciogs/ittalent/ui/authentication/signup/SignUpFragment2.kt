package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentSignUp2Binding
import mx.mauriciogs.ittalent.ui.authentication.SignInException
import mx.mauriciogs.ittalent.ui.authentication.SignUpExeption
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.*

class SignUpFragment2 : BaseFrag<FragmentSignUp2Binding>(R.layout.fragment_sign_up2) {

    private lateinit var mBinding: FragmentSignUp2Binding

    private val signUpViewModel: SignUpViewModel by viewModels(){ SignUpViewModel.SignUpFragmentsVMFactory() }

    override fun FragmentSignUp2Binding.initialize() {
        mBinding = this
        signUpViewModel.getUserSignUpCredentials()
        initObservers()
    }

    private fun initObservers() {
        signUpViewModel.userSignUp.observe(requireActivity()) {
            initUI(it?:return@observe)
        }
        signUpViewModel.signUpUIModel.observe(requireActivity()) {
            signUpUi(it?: return@observe)
        }
    }

    private fun initUI(userSignUpCredentials: UserSignUpCredentials) {
        with(mBinding) {
            when (userSignUpCredentials.userType) {
                Int.default() -> { tvWelcome.text = getString(R.string.welcome_talent) }
                Int.TALENT_UT() -> { tvWelcome.text = getString(R.string.welcome_talent) }
                Int.ENTERPRISE_R_UT() -> { tvWelcome.text = getString(R.string.welcome_recruiter) }
                Int.PROJECT_R_UT() -> { tvWelcome.text = getString(R.string.welcome_recruiter) }
            }

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
            signUpViewModel.signUpEmail(password, passConfirm, fullName)
        }
    }

    private fun signUpUi(signUpUIModel: SignUpUIModel) = signUpUIModel.run{
        if (enableNextStep) {
            signUpViewModel.stopButtonContinue()
            Log.d("Success", "Datos correctos")
            // findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpFragment2())
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
