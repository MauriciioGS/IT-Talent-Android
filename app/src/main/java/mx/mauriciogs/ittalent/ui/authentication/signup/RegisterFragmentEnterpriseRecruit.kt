package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Intent
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.safeNavigate
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmentRegisterEnterpriseBinding
import mx.mauriciogs.ittalent.ui.main.MainActivity

@AndroidEntryPoint
class RegisterFragmentEnterpriseRecruit : BaseFrag<FragmentRegisterEnterpriseBinding>(R.layout.fragment_register_enterprise) {

    private lateinit var mBinding: FragmentRegisterEnterpriseBinding

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun FragmentRegisterEnterpriseBinding.initialize() {
        mBinding = this
        initUI()
        initObservers()

    }

    private fun initObservers() {
        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) showError(it.exception)
            if (it.success != null) showSignInSuccess(it.success)
        }
    }

    private fun initUI() {
        with(mBinding) {
            button.setOnClickListener {
                showInfoDialog()
            }
            btnSave.setOnClickListener {
                checkFields()
            }
        }
    }

    private fun checkFields() {
        with(mBinding) {
            when{
                etEmpresa.text.toString().isEmpty() -> { emptyField(etEmpresa, R.string.txt_empresarec)}
                dropdownMenuRol.text.toString().isEmpty() -> emptyFieldList(dropdownMenuRol, R.string.txt_no_rolerec)
                else -> {
                    val empresa = etEmpresa.text.toString()
                    val rol = dropdownMenuRol.text.toString()

                    signUpViewModel.signUpRecruiter(empresa, rol)
                }
            }
        }
    }

    private fun showSignInSuccess(userType: Int) {
        Log.d("LOGIN","Usuario loggeado y se creo en firestore")
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            putExtra("userType", userType)
        }
        startActivity(intent).apply { requireActivity().finish() }
    }

    private fun showError(exception: Exception) {
        snackbar(exception.message).showError()
        findNavControllerSafely()?.safeNavigate(SignUpFragment3TalentDirections.actionGlobalSignInFragment())
    }

    private fun emptyField(editText: TextInputEditText, errorId: Int) {
        editText.error = getString(errorId)
        snackbar(errorId).showError()
    }

    private fun emptyFieldList(dropMenu: AutoCompleteTextView, errorId: Int) {
        dropMenu.error = getString(errorId)
        snackbar(getString(errorId)).showError()
    }

    private fun showInfoDialog() {

    }

}