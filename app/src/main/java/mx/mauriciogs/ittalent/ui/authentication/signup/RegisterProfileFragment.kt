package mx.mauriciogs.ittalent.ui.authentication.signup

import android.net.Uri
import android.widget.AutoCompleteTextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterProfileBinding
import com.google.android.material.textfield.TextInputEditText
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.empty
import mx.mauriciogs.ittalent.ui.global.extensions.showError
import mx.mauriciogs.ittalent.ui.global.extensions.showInfo
import mx.mauriciogs.ittalent.ui.global.extensions.snackbar

class RegisterProfileFragment : BaseFrag<FragmentRegisterProfileBinding>(R.layout.fragment_register_profile) {
    private lateinit var mBinding: FragmentRegisterProfileBinding

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) saveUri(uri)
        else snackbar(R.string.info_foto).showInfo()
    }

    var photoUri = String.empty()

    override fun FragmentRegisterProfileBinding.initialize() {
        mBinding = this
        signUpViewModel.getUserSignUpCredentials()
        initObservers()
    }

    private fun initObservers() {
        signUpViewModel.stopButtonContinue()
        signUpViewModel.userSignUp.observe(requireActivity()) { initUI(it) }

    }

    private fun initUI(userCredentials: UserSignUpCredentials?) {
        with(mBinding) {
            if (userCredentials != null) {
                etName.setText(userCredentials.fullName)
                etCorreo.setText(userCredentials.email)
            }

            btnFoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnSave.setOnClickListener { checkFields() }
        }
    }

    private fun checkFields() {
        with(mBinding) {
            when {
                etName.text.toString().isEmpty() -> emptyField(etName, R.string.txt_name)
                dropdownMenuPais.text.toString().isEmpty() -> emptyFieldList(dropdownMenuPais,
                    R.string.txt_require_pais
                )
                etCiudad.text.toString().isEmpty() -> emptyField(etCiudad, R.string.txt_ciudad_p)
                etEdad.text.toString().isEmpty() -> emptyField(etName, R.string.txt_require_edad)
                etEdad.text.toString().toInt() < 18 -> snackbar(R.string.txt_mayor_edad).showError()
                etPhoNum.text.toString().isEmpty() || etPhoNum.text.toString().length < 10 -> emptyField(etPhoNum,
                    R.string.txt_requite_phonenum
                )
                etAbout.text.toString().isEmpty() -> emptyField(etAbout, R.string.txt_require_desc)
                photoUri.isEmpty() -> snackbar(R.string.txt_no_foto).showError()
                else -> {
                    val nombre = etName.text.toString()
                    val pais = dropdownMenuPais.text.toString()
                    val ciudad = etCiudad.text.toString()
                    val edad = etEdad.text.toString().toInt()
                    val numTel = etPhoNum.text.toString()
                    val resumen = etAbout.text.toString()

                    signUpViewModel.signUpProfile(nombre, pais, ciudad, edad, numTel, resumen, photoUri)
                }
            }
        }
    }

    private fun saveUri(uri: Uri) {
        mBinding.ivProfilePhoto.setImageURI(uri)
        photoUri = uri.toString()
    }

    private fun emptyField(editText: TextInputEditText, errorId: Int) {
        editText.error = getString(errorId)
        snackbar(errorId).showError()
    }

    private fun emptyFieldList(dropMenu: AutoCompleteTextView, errorId: Int) {
        dropMenu.error = getString(errorId)
        snackbar(getString(errorId)).showError()
    }
}