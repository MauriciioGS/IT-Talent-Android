package mx.mauriciogs.ittalent.ui.authentication.signin

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmentSignInBinding
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.main.MainActivity
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment

class SignInFragment: BaseFrag<FragmentSignInBinding>(R.layout.fragment_sign_in) {

    private lateinit var mBinding : FragmentSignInBinding

    private val signInViewModel : SignInViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    override fun FragmentSignInBinding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }
        signInViewModel.signInUiModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) snackbar(it.exception.message).showError()
            if (it.singInSuccess != null) showLogin(it.singInSuccess.userType)
        }
    }

    private fun showLogin(userType: Int?) {
        if (userType != null) {
            startActivity(Intent(requireActivity(), MainActivity::class.java)).apply {
                requireActivity().finish()
            }
        }
    }

    private fun initListeners() {
        with(mBinding) {
            btnSignUp.setOnClickListener {
                findNavControllerSafely()?.safeNavigate(SignInFragmentDirections.actionSignInFragmentToWelcomeFragment())
            }
            btnContinue.setOnClickListener { checkFields() }
        }
    }

    private fun checkFields() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

        val userCredentials = Credentials(mBinding.etEmail.text.toString(), mBinding.etPass.text.toString())
        signInViewModel.signInEmailPass(userCredentials)
    }

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@SignInFragment.childFragmentManager.executePendingTransactions()
        if(!this@SignInFragment.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@SignInFragment.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@SignInFragment.findChildFragmentByTag(
        WelcomeFragment.lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }

}

