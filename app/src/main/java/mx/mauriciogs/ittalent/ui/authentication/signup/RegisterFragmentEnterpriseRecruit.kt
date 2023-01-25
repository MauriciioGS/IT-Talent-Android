package mx.mauriciogs.ittalent.ui.authentication.signup

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
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
        findNavControllerSafely()
            ?.safeNavigate(RegisterFragmentEnterpriseRecruitDirections.actionRegisterFragmentEnterpriseRecruitToSignInFragment())
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
        MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
            .setTitle("Registrar una empresa")
            .setMessage("Si tu empresa es nueva por aquí y deseas registrarla, envíanos un email con más " +
                    "información de la empresa como tados fiscales y el logo de la empresa para personalizar tus ofertas")
            .setPositiveButton(R.string.btn_envia_correo) { dialog, _ ->
                sendEmail(arrayOf("hector.garcias@ingenieria.unam.edu"), "Registro de nueva empresa")
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendEmail(to: Array<String>, asunto: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, asunto)
        }
        getResult.launch(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_CANCELED -> {

            }
            else -> {}
        }
    }

}
