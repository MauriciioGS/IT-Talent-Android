package mx.mauriciogs.ittalent.ui.recruitment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.showInfo
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmenOpenApplicantsBinding
import mx.mauriciogs.ittalent.domain.talent.Talent

private const val JOB_KEY = "jobID"

class OpenApplicantsFragment: BaseFrag<FragmenOpenApplicantsBinding>(R.layout.fragmen_open_applicants) {

    private lateinit var mBinding: FragmenOpenApplicantsBinding

    private val recruitmentViewModel: RecruitmentViewModel by activityViewModels()

    override fun FragmenOpenApplicantsBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        requireArguments().getString(JOB_KEY)?.let { recruitmentViewModel.getApplicants(it) }
        initObservers()
    }

    private fun initObservers() {
        recruitmentViewModel.recruitmentUiModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) requireActivity().snackbar(it.exception.message).showError()
            if (it.showSuccessGetApplicants != null) initUi(it.showSuccessGetApplicants)
        }
    }

    private fun initUi(applicants: MutableList<Talent>) {
        mBinding.noDataAnim2.visibility = View.GONE
        mBinding.rvItems.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = TalentApplicantsAdapter(applicants, this@OpenApplicantsFragment)
        }
    }

    fun onClickItem(talent: Talent) {
        requireActivity().snackbar("${talent.email}").showInfo()
    }

}
