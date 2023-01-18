package mx.mauriciogs.ittalent.ui.authentication.signup

import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterXPBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import mx.mauriciogs.ittalent.ui.authentication.signup.util.Experience
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.*
import org.joda.time.Years

class RegisterXPFragment : BaseFrag<FragmentRegisterXPBinding>(R.layout.fragment_register_x_p) {
    private lateinit var mBinding: FragmentRegisterXPBinding

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    private lateinit var meses: Array<String>
    private lateinit var anios: Array<String>
    private var firstItemMod = String.empty()
    private var firstItemTipo = String.empty()

    override fun FragmentRegisterXPBinding.initialize() {
        mBinding = this
        initObservers()
        initUI()
    }

    private fun initObservers() {
        signUpViewModel.stopExperienceSuccess()
        signUpViewModel.stopButtonContinue()
        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) { signUpUi(it?: return@observe) }
    }

    private fun signUpUi(signUpUIModel: SignUpUIModel) { if (signUpUIModel.successExperience) anotherXp() }

    private fun initUI() {
        with(mBinding) {

            firstItemMod = resources.getStringArray(R.array.modal)[0]
            dropdownMenuModalidad.setText(firstItemMod, false)
            firstItemTipo = resources.getStringArray(R.array.type_job)[0]
            dropdownMenuTipo.setText(firstItemTipo, false)

            meses = resources.getStringArray(R.array.months)
            anios = resources.getStringArray(R.array.years)

            etCargo.requestFocus()

            btnSave.setOnClickListener {
                checkFields()
            }
        }
    }

    private fun checkFields() {
        with(mBinding) {
            when {
                etCargo.text.toString().isEmpty() -> emptyField(etCargo, R.string.txt_no_cargo)
                etEmpresa.text.toString().isEmpty() -> emptyField(etEmpresa, R.string.txt_no_empresa)
                etCiudad.text.toString().isEmpty() -> emptyField(etCiudad, R.string.txt_no_ciudad)
                dropdownMenuModalidad.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuModalidad,
                    R.string.txt_no_modalidad,
                    etEmpresa.text.toString()
                )
                dropdownMenuTipo.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuTipo,
                    R.string.txt_no_tipo,
                    etEmpresa.text.toString()
                )
                dropdownMenuMesI.text.toString().isEmpty() || dropdownMenuYearI.text.toString().isEmpty() -> {
                    emptyFieldList(dropdownMenuMesI, R.string.txt_no_inicio, etEmpresa.text.toString())
                    emptyFieldList(dropdownMenuYearI, R.string.txt_no_inicio, etEmpresa.text.toString())
                }
                etAbout.text.toString().isEmpty() -> emptyField(etAbout, R.string.txt_no_logros)

                else -> {
                    val cargo = etCargo.text.toString()
                    val empresa = etEmpresa.text.toString()
                    val ciudad = etCiudad.text.toString()
                    val modalidad = dropdownMenuModalidad.text.toString()
                    val tipo = dropdownMenuTipo.text.toString()
                    val mesInicio = dropdownMenuMesI.text.toString()
                    val anioInicio = dropdownMenuYearI.text.toString()
                    var yearsXP = Int.default()
                    var periodo = String.empty()
                    if (swIWorkHere.isChecked) {
                        yearsXP = getExperienceByYears(mesInicio, anioInicio, isNow = true)
                        periodo = "$mesInicio $anioInicio - Actualidad"
                    } else {
                        if (dropdownMenuMesF.text.toString().isEmpty() || dropdownMenuYearF.text.toString().isEmpty()) {
                            emptyFieldList(dropdownMenuMesF, R.string.txt_no_fin, etEmpresa.text.toString())
                            emptyFieldList(dropdownMenuYearF, R.string.txt_no_fin, etEmpresa.text.toString())
                            return
                        } else {
                            val mesFin = dropdownMenuMesF.text.toString()
                            val anioFin = dropdownMenuYearF.text.toString()
                            yearsXP = getExperienceByYears(mesInicio, anioInicio, mesFin, anioFin)
                            periodo = "$mesInicio $anioInicio - $mesFin $anioFin"
                        }
                    }
                    val desc = etAbout.text.toString()

                    val experience = Experience(
                        charge = cargo,
                        enterprise = empresa,
                        city = ciudad,
                        mode = modalidad,
                        type = tipo,
                        period = periodo,
                        yearsXp = yearsXP,
                        nowadays = swIWorkHere.isChecked,
                        achievements = desc
                    )
                    signUpViewModel.setExperience(experience)
                }
            }
        }
    }

    private fun getExperienceByYears(mes1: String, anio1: String, mes2: String = String.empty(), anio3: String = String.empty(), isNow: Boolean = false): Int {
        var xpYears = Int.default()

        var month = meses.indexOf(mes1)+1
        var year = anios[anios.indexOf(anio1)].toInt()
        val day = Int.one()
        val from = org.joda.time.LocalDate(year, month, day)

        if (isNow) {
            val toNow = org.joda.time.LocalDate()
            val years = Years.yearsBetween(from, toNow)
            xpYears = years.years
        } else {
            month = meses.indexOf(mes2)+Int.one()
            year = anios[anios.indexOf(anio3)].toInt()
            val to = org.joda.time.LocalDate(year, month, day)
            val years = Years.yearsBetween(from, to)
            xpYears = years.years
        }

        return xpYears
    }

    private fun emptyField(editText: TextInputEditText, errorId: Int) {
        editText.error = getString(errorId)
        snackbar(errorId).showError()
    }

    private fun emptyFieldList(dropMenu: AutoCompleteTextView, errorId: Int, empresa: String) {
        dropMenu.error = getString(errorId, empresa)
        snackbar(getString(errorId, empresa)).showError()
    }

    private fun anotherXp() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.txt_saved_data)
            .setMessage(R.string.txt_desc_xp)
            .setPositiveButton(R.string.txt_yes) { dialog, _ ->
                dataSaved()
                dialog.dismiss()
                mBinding.etCargo.requestFocus()
            }
            .setNegativeButton(R.string.btn_continue) { dialog, _ ->
                dialog.dismiss()
                signUpViewModel.stopButtonContinue(true)
            }
            .show()
    }

    private fun dataSaved() {
        with(mBinding) {
            etCargo.text?.clear()
            etEmpresa.text?.clear()
            etCiudad.text?.clear()
            etAbout.text?.clear()
            dropdownMenuModalidad.text.clear()
            dropdownMenuTipo.text.clear()
            dropdownMenuMesI.text.clear()
            dropdownMenuMesF.text.clear()
            dropdownMenuYearI.text.clear()
            dropdownMenuYearF.text.clear()
        }
    }

}