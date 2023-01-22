package mx.mauriciogs.ittalent.ui.profile

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmentMyProfileRecBinding
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

class MyProfileRecFragment: BaseFrag<FragmentMyProfileRecBinding>(R.layout.fragment_my_profile_rec) {

    private lateinit var mBinding: FragmentMyProfileRecBinding

    private val myProfileViewModel: MyProfileViewModel by activityViewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) saveUri(uri)
        else snackbar(R.string.info_foto).showInfo()
    }

    var photoUri = String.empty()
    private var isEditEnabled = Boolean.no()

    override fun FragmentMyProfileRecBinding.initialize() {
        mBinding = this
        showToolBar(true)
        showFloatingActionBtn(show = false)
        myProfileViewModel.getProfile()
        initObservers()
    }

    private fun initObservers() {
        myProfileViewModel.myProfileModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.setUI != null) setUI(it.setUI)
            if (it.exception != null) showError(it.exception)
            if (it.showSuccess != null) {
                mBinding.swEditar.isChecked = false
                mBinding.etName.requestFocus()
                requireActivity().snackbar("Perfil acualizado!").showSuccess()
            }
        }
    }

    private fun setUI(profile: UserProfile) {
        with(mBinding) {
            photoUri = profile.photoUrl!!
            Glide.with(requireActivity())
                .load(profile.photoUrl)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        mBinding.ivProfilePhoto.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.user_icon_active))
                        return false
                    }
                })
                .into(ivProfilePhoto)
            etName.setText(profile.fullName)
            dropdownMenuPais.setText(profile.country, false)
            etCiudad.setText(profile.city)
            etEdad.setText(profile.age.toString())
            etPhoNum.setText(profile.phoneNum)
            etCorreo.setText(profile.email)
            etAbout.setText(profile.resume)
            etEmpresa.setText(profile.enterprise)
            dropdownMenuRol.setText(profile.role, false)

            btnChange.setOnClickListener {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.txt_head_cambiar_empresa)
                    .setMessage(R.string.txt_cambiar_empresa)
                    .setPositiveButton(R.string.txt_eliminar_cuenta) { dialog, _ ->
                        dialog.dismiss()
                        //myProfileViewModel.deleteProfile()
                    }
                    .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            swEditar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    isEditEnabled = Boolean.yes()
                    btnSave.setBackgroundColor(requireActivity().getColor(R.color.secondary))
                    btnFoto.visibility = View.VISIBLE
                    btnFoto.setOnClickListener {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
                else {
                    btnSave.setBackgroundColor(requireActivity().getColor(R.color.disable))
                    btnFoto.visibility = View.INVISIBLE
                    isEditEnabled = Boolean.no()
                }
            }

            btnSave.setOnClickListener {
                if (!isEditEnabled) requireActivity().snackbar(R.string.txt_error_editar).showError()
                else checkFields()
            }
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
                etEdad.text.toString().toInt() < 18 -> requireActivity().snackbar(R.string.txt_mayor_edad).showError()
                etPhoNum.text.toString().isEmpty() || etPhoNum.text.toString().length < 10 -> emptyField(etPhoNum,
                    R.string.txt_requite_phonenum
                )
                etAbout.text.toString().isEmpty() -> emptyField(etAbout, R.string.txt_require_desc)
                etEmpresa.text.toString().isEmpty() -> emptyField(etEmpresa, R.string.txt_no_empresa)
                dropdownMenuRol.text.toString().isEmpty() -> emptyFieldList(dropdownMenuRol,
                    R.string.txt_no_rolerec
                )
                photoUri.isEmpty() -> requireActivity().snackbar(R.string.txt_no_foto).showError()
                else -> {
                    val nombre = etName.text.toString()
                    val pais = dropdownMenuPais.text.toString()
                    val ciudad = etCiudad.text.toString()
                    val edad = etEdad.text.toString().toInt()
                    val numTel = etPhoNum.text.toString()
                    val resumen = etAbout.text.toString()
                    val rol = dropdownMenuRol.text.toString()

                    myProfileViewModel.updateProfile(nombre, pais, ciudad, edad, numTel, resumen, photoUri, rol)
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

    private fun showError(exception: Exception?) {
        if (exception != null) {
            requireActivity().snackbar(exception.message).showError()
        }
    }

}
