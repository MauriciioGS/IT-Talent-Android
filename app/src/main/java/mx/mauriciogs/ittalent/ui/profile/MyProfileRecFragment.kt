package mx.mauriciogs.ittalent.ui.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.init.InitActivity
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment

private val lostConnBottomSheetTag: String = LostConnectionFragment::class.java.simpleName

class MyProfileRecFragment :
    BaseFrag<FragmentMyProfileRecBinding>(R.layout.fragment_my_profile_rec) {

    private lateinit var mBinding: FragmentMyProfileRecBinding

    private val myProfileRecViewModel: MyProfileRecViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) saveUri(uri)
            else snackbar(R.string.info_foto).showInfo()
        }

    var photoUri = String.empty()
    private var isEditEnabled = Boolean.no()

    override fun FragmentMyProfileRecBinding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
        showToolBar(true)
        showFloatingActionBtn(show = false)
        initObservers()
        myProfileRecViewModel.getProfile()
    }

    private fun initObservers() {
        myProfileRecViewModel.myProfileModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.setUI != null) setUI(it.setUI)
            if (it.exception != null) showError(it.exception)
            if (it.showSuccess != null) {
                mBinding.swEditar.isChecked = false
                mBinding.etName.requestFocus()
                requireActivity().snackbar("Perfil acualizado!").showSuccess()
            }
        }
        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }
    }

    private fun setUI(profile: UserProfile) {
        with(mBinding) {
            if (profile.userType != Int.TALENT_UT()) {
                enterpriseEdit.visibility = View.VISIBLE
                roleEdit.visibility = View.GONE
            } else {
                roleEdit.visibility = View.VISIBLE
                enterpriseEdit.visibility = View.GONE
            }
            photoUri = profile.photoUrl!!
            Glide.with(requireActivity())
                .load(profile.photoUrl)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        mBinding.ivProfilePhoto.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireActivity(),
                                R.drawable.user_icon_active
                            )
                        )
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
            dropdownMenuRolProf.setText(profile.profRole, false)

            btnChange.setOnClickListener {
                MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
                    .setTitle(R.string.txt_head_cambiar_empresa)
                    .setMessage(R.string.txt_cambiar_empresa)
                    .setPositiveButton(R.string.txt_eliminar_cuenta) { dialog, _ ->
                        dialog.dismiss()
                        myProfileRecViewModel.deleteProfile()
                    }
                    .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            btnDelete.setOnClickListener {
                findNavControllerSafely()?.safeNavigate(MyProfileRecFragmentDirections.actionMyProfileFragmentToRetentionFragment())
            }

            swEditar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    isEditEnabled = Boolean.yes()
                    btnSave.setBackgroundColor(requireActivity().getColor(R.color.secondary))
                    btnFoto.visibility = View.VISIBLE
                    dropdownMenuRolProf.setAdapter(ArrayAdapter(
                        requireActivity(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.roles)))
                    btnFoto.setOnClickListener {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                } else {
                    btnSave.setBackgroundColor(requireActivity().getColor(R.color.disable))
                    btnFoto.visibility = View.INVISIBLE
                    isEditEnabled = Boolean.no()
                }
            }

            btnSave.setOnClickListener {
                if (!isEditEnabled) requireActivity().snackbar(R.string.txt_error_editar)
                    .showError()
                else{
                    if (profile.userType != Int.TALENT_UT())
                        checkFieldsRec()
                    else
                        checkFieldsTal()
                }
            }
        }
    }

    private fun checkFieldsRec() {
        with(mBinding) {
            when {
                etName.text.toString().isEmpty() -> emptyField(etName, R.string.txt_name)
                dropdownMenuPais.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuPais,
                    R.string.txt_require_pais
                )
                etCiudad.text.toString().isEmpty() -> emptyField(etCiudad, R.string.txt_ciudad_p)
                etEdad.text.toString().isEmpty() -> emptyField(etName, R.string.txt_require_edad)
                etEdad.text.toString()
                    .toInt() < 18 -> requireActivity().snackbar(R.string.txt_mayor_edad).showError()
                etPhoNum.text.toString()
                    .isEmpty() || etPhoNum.text.toString().length < 10 -> emptyField(
                    etPhoNum,
                    R.string.txt_requite_phonenum
                )
                etAbout.text.toString().isEmpty() -> emptyField(etAbout, R.string.txt_require_desc)
                etEmpresa.text.toString().isEmpty() -> emptyField(
                    etEmpresa,
                    R.string.txt_no_empresa
                )
                dropdownMenuRol.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuRol,
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

                    myProfileRecViewModel.updateProfile(nombre, pais, ciudad, edad, numTel, resumen,
                        photoUri, rol, String.empty())
                }
            }
        }
    }

    private fun checkFieldsTal() {
        with(mBinding) {
            when {
                etName.text.toString().isEmpty() -> emptyField(etName, R.string.txt_name)
                dropdownMenuPais.text.toString().isEmpty() ->
                    emptyFieldList(dropdownMenuPais, R.string.txt_require_pais)
                etCiudad.text.toString().isEmpty() -> emptyField(etCiudad, R.string.txt_ciudad_p)
                etEdad.text.toString().isEmpty() -> emptyField(etName, R.string.txt_require_edad)
                etEdad.text.toString()
                    .toInt() < 18 -> requireActivity().snackbar(R.string.txt_mayor_edad).showError()
                etPhoNum.text.toString().isEmpty() || etPhoNum.text.toString().length < 10 ->
                        emptyField(etPhoNum, R.string.txt_requite_phonenum)
                etAbout.text.toString().isEmpty() -> emptyField(etAbout, R.string.txt_require_desc)
                photoUri.isEmpty() -> requireActivity().snackbar(R.string.txt_no_foto).showError()
                dropdownMenuRolProf.text.toString().isEmpty() ->
                    emptyFieldList(dropdownMenuPais, R.string.txt_require_pais)
                else -> {
                    val nombre = etName.text.toString()
                    val pais = dropdownMenuPais.text.toString()
                    val ciudad = etCiudad.text.toString()
                    val edad = etEdad.text.toString().toInt()
                    val numTel = etPhoNum.text.toString()
                    val resumen = etAbout.text.toString()
                    val profRole = dropdownMenuRolProf.text.toString()

                    myProfileRecViewModel.updateProfile(nombre, pais, ciudad, edad, numTel, resumen,
                        photoUri,String.empty(), profRole)
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

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@MyProfileRecFragment.childFragmentManager.executePendingTransactions()
        if(!this@MyProfileRecFragment.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@MyProfileRecFragment.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@MyProfileRecFragment.findChildFragmentByTag(
        lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }

}
