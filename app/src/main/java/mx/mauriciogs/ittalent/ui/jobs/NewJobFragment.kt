package mx.mauriciogs.ittalent.ui.jobs

import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmentNewJobBinding
import mx.mauriciogs.ittalent.domain.jobs.Job

class NewJobFragment: BaseFrag<FragmentNewJobBinding>(R.layout.fragment_new_job) {

    private lateinit var mBinding: FragmentNewJobBinding

    private val newJobViewModel: NewJobViewModel by activityViewModels()

    private var firstItemMon = String.empty()

    override fun FragmentNewJobBinding.initialize() {
        mBinding = this
        showCollapsingToolBar()
        initCloseBntListener(findNavControllerSafely())
        newJobViewModel.getProfile()
        initObservers()
    }

    private fun initObservers() {
        newJobViewModel.jobsUiModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.setUI != null) initUi(it.setUI)
            if (it.exception != null) snackbar(it.exception.message).showError()
            if (it.showSuccess != null) showPostedSuccess()
        }
    }

    private fun showPostedSuccess() {
        requireActivity().snackbar("Empleo publicado!").showSuccess()
        findNavControllerSafely()?.popBackStack()
    }

    private fun initUi(empresaDatos: String) {
        firstItemMon = resources.getStringArray(R.array.coin)[0]
        mBinding.dropdownCoin.setText(firstItemMon, false)
        mBinding.etEnterprise.setText(empresaDatos)

        with(mBinding) {
            btnContinue.setOnClickListener { checkFields() }
        }
    }

    private fun checkFields() {
        with(mBinding) {
            when {
                etCharge.text.toString().isEmpty() -> emptyField(etCharge, R.string.txt_no_cargo)
                etLocation.text.toString().isEmpty() -> emptyField(etLocation, R.string.txt_no_ciudad_ne)
                etVacancy.text.toString().isEmpty() -> emptyField(etVacancy, R.string.txt_no_vacantes)
                dropdownMenuModalidad.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuModalidad,
                    R.string.txt_no_modalidad_ne)
                dropdownMenuTipo.text.toString().isEmpty() -> emptyFieldList(
                    dropdownMenuTipo,
                    R.string.txt_no_tipo_ne)
                dropdownWage.text.toString().isEmpty() -> emptyFieldList(
                    dropdownWage,
                    R.string.txt_no_salario)
                dropdownCoin.text.toString().isEmpty() -> emptyFieldList(
                    dropdownWage,
                    R.string.txt_no_moneda)
                else -> {
                    val cargo = etCharge.text.toString()
                    val empresa = etEnterprise.text.toString()
                    val ubicacion = etLocation.text.toString()
                    val modalidad = dropdownMenuModalidad.text.toString()
                    val tipo = dropdownMenuTipo.text.toString()
                    val vacantes = etVacancy.text.toString()
                    val salario = dropdownWage.text.toString()
                    val moneda = dropdownCoin.text.toString()
                    val rangoSalario = "$salario $moneda"

                    val empleo = Job(job = cargo, enterprise = empresa, location = ubicacion, mode = modalidad,
                        type = tipo, wage = rangoSalario, vacancies = vacantes)
                    Log.d("POSTJOB", "$empleo")

                    newJobViewModel.postJob(empleo)
                }
            }
        }
    }

    private fun emptyField(editText: TextInputEditText, errorId: Int) {
        editText.error = getString(errorId)
        requireActivity().snackbar(errorId).showError()
    }

    private fun emptyFieldList(dropMenu: AutoCompleteTextView, errorId: Int) {
        dropMenu.error = getString(errorId)
        requireActivity().snackbar(getString(errorId)).showError()
    }

}
