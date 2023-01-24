package mx.mauriciogs.ittalent.ui.recruitment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmenOpenApplicantsBinding
import mx.mauriciogs.ittalent.domain.talent.Talent

private const val JOB_KEY = "jobID"

class OpenApplicantsFragment: BaseFrag<FragmenOpenApplicantsBinding>(R.layout.fragmen_open_applicants) {

    private lateinit var mBinding: FragmenOpenApplicantsBinding

    private val recruitmentViewModel: RecruitmentViewModel by activityViewModels()

    private var talent = mutableListOf<Talent>()
    private var listAdaper = TalentApplicantsAdapter(talent, this)
    private var talentSelected = mutableListOf<Talent>()

    override fun FragmenOpenApplicantsBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        initRecycler()
        requireArguments().getString(JOB_KEY)?.let { recruitmentViewModel.getApplicants(it) }
        initObservers()
    }

    private fun initObservers() {
        recruitmentViewModel.recruitmentUiModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) requireActivity().snackbar(it.exception.message).showError()
            if (it.showSuccessGetApplicants != null) initUi(it.showSuccessGetApplicants)
            if (it.showSuccessUpdate != null) {
                findNavControllerSafely()?.popBackStack()
            }
        }
    }

    private fun initRecycler() {
        mBinding.rvItems.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = listAdaper
        }
    }

    private fun initUi(applicants: MutableList<Talent>) {
        mBinding.btnOpenAll.text = getString(R.string.btn_next_step, talentSelected.size)
        listAdaper =  TalentApplicantsAdapter(applicants, this)
        mBinding.noDataAnim2.visibility = View.GONE
        mBinding.rvItems.adapter = listAdaper

        mBinding.btnOpenAll.setOnClickListener {
            if (talentSelected.isEmpty())
                requireActivity().snackbar("Selecciona a los talentos que pasan a la siguiente etapa").showError()
            else
                requireArguments().getString(JOB_KEY)
                    ?.let { it1 -> recruitmentViewModel.setApplicantsNextStep(talentSelected, it1) }
        }
    }

    fun onClickItemOk(talent: Talent) {
        talentSelected.add(talent)
        mBinding.btnOpenAll.text = getString(R.string.btn_next_step, talentSelected.size)
    }

    fun onClickItemNo(talent: Talent) {
        talentSelected.remove(talent)
        mBinding.btnOpenAll.text = getString(R.string.btn_next_step, talentSelected.size)
    }

}
