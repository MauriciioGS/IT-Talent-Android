package mx.mauriciogs.ittalent.ui.authentication.signup

import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmentRegisterEnterpriseBinding

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