package mx.mauriciogs.ittalent.ui.authentication.signup

import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterXPBinding
import com.google.android.material.textfield.TextInputEditText
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.default
import mx.mauriciogs.ittalent.ui.global.extensions.empty
import mx.mauriciogs.ittalent.ui.global.extensions.showError
import mx.mauriciogs.ittalent.ui.global.extensions.snackbar
import org.joda.time.Years
import java.text.SimpleDateFormat

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
        signUpViewModel.stopButtonContinue()

    }
    private fun initUI() {
        with(mBinding) {

            firstItemMod = resources.getStringArray(R.array.modal)[0]
            dropdownMenuModalidad.setText(firstItemMod, false)
            firstItemTipo = resources.getStringArray(R.array.type_job)[0]
            dropdownMenuTipo.setText(firstItemTipo, false)

            meses = resources.getStringArray(R.array.months)
            anios = resources.getStringArray(R.array.years)

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
                !swIWorkHere.isChecked -> {
                    if (dropdownMenuMesF.text.toString().isEmpty() || dropdownMenuYearF.text.toString().isEmpty()) {
                        emptyFieldList(dropdownMenuMesF, R.string.txt_no_fin, etEmpresa.text.toString())
                        emptyFieldList(dropdownMenuYearF, R.string.txt_no_fin, etEmpresa.text.toString())
                    }
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
                        val mesFin = dropdownMenuMesF.text.toString()
                        val anioFin = dropdownMenuYearF.text.toString()
                        yearsXP = getExperienceByYears(mesInicio, anioInicio, mesFin, anioFin)
                        periodo = "$mesInicio $anioInicio - $mesFin $anioFin"
                    }
                    val desc = etAbout.text.toString()
                    dataSaved()
                }
            }
        }
    }

    private fun getExperienceByTime(date1: String, date2: String) {
        val formatter = SimpleDateFormat("yyyy/mm/dd")
        val from = formatter.parse(date1)
        val to = formatter.parse(date2)

    }

    private fun getExperienceByYears(mes1: String, anio1: String, mes2: String = String.empty(), anio3: String = String.empty(), isNow: Boolean = false): Int {
//        val index = meses.indexOf(mes)+1
//        val mm = if (index < 10) "0$index" else "$index"
//        val yyyy = anios[anios.indexOf(anio)].toInt()
//        return "$yyyy/$mm/01"
        var xpYears = 0

        var month = meses.indexOf(mes1)+1
        var year = anios[anios.indexOf(anio1)].toInt()
        val day = 1
        val from = org.joda.time.LocalDate(year, month, day)

        if (isNow) {
            val toNow = org.joda.time.LocalDate()
            val years = Years.yearsBetween(from, toNow)
            xpYears = years.years
        } else {
            month = meses.indexOf(mes2)+1
            year = anios[anios.indexOf(anio3)].toInt()
            val to = org.joda.time.LocalDate(year, month, day)
            val years = Years.yearsBetween(from, to)
            xpYears = years.years
        }

        Log.d("XPYEARS", "$xpYears")

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

    private fun dataSaved() {
        Toast.makeText(
            requireContext(),
            "Succesfull",
            Toast.LENGTH_SHORT
        ).show()

        with(mBinding) {
            etCargo.text?.clear()
            etEmpresa.text?.clear()
            etCiudad.text?.clear()
            etAbout.text?.clear()
            dropdownMenuModalidad.setText(firstItemMod)
            dropdownMenuTipo.setText(firstItemTipo)
            dropdownMenuMesI.text?.clear()
            dropdownMenuMesF.text?.clear()
            dropdownMenuYearI.text?.clear()
            dropdownMenuYearF.text?.clear()
        }
    }

}