package mx.mauriciogs.ittalent.ui.jobs.apply

import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.safeNavigate
import mx.mauriciogs.ittalent.databinding.FragmentNewApplicantBinding

class NewApplicantFragment: BaseFrag<FragmentNewApplicantBinding>(R.layout.fragment_new_applicant) {

    private lateinit var mBinding: FragmentNewApplicantBinding

    override fun FragmentNewApplicantBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initUI()
    }

    private fun initUI() {
        val jobName = NewApplicantFragmentArgs.fromBundle(requireArguments()).jobName
        with(mBinding) {
            tvJob.text = requireActivity().getString(R.string.txt_te_has_postulado, jobName)

            btnContinue.setOnClickListener {
                findNavControllerSafely()?.safeNavigate(NewApplicantFragmentDirections.actionNewApplicantFragmentToJobsFragment())
            }
        }
    }
}