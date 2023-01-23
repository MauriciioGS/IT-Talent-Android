package mx.mauriciogs.ittalent.ui.jobs.apply

import androidx.fragment.app.activityViewModels
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.safeNavigate
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmentApplyJobBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import org.joda.time.*

class ApplyJobFragment: BaseFrag<FragmentApplyJobBinding>(R.layout.fragment_apply_job) {

    private lateinit var mBinding: FragmentApplyJobBinding

    private val applyJobViewModel: ApplyJobViewModel by activityViewModels()

    private lateinit var job: Job
    private lateinit var applicantEmail: String

    override fun FragmentApplyJobBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        initUI()
        initObservers()
    }

    private fun initObservers() {
        applyJobViewModel.appliJobModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) requireActivity().snackbar(it.exception.message).showError()
            if (it.showSuccess != null) {
                findNavControllerSafely()
                    ?.safeNavigate(ApplyJobFragmentDirections.actionApplyJobFragmentToNewApplicantFragment(it.showSuccess))
                applyJobViewModel.stopSuccess()
            }

        }
    }

    private fun initUI() {
        job = ApplyJobFragmentArgs.fromBundle(requireArguments()).job
        applicantEmail = ApplyJobFragmentArgs.fromBundle(requireArguments()).userEmail
        with(mBinding) {
            tvJob.text = job.job
            tvVacancies.text = requireActivity().getString(R.string.txt_rvjobs_vacantes, job.vacancies)
            tvEnterprise.text = requireActivity().getString(R.string.txt_rvjobs_empresa, job.enterprise)
            tvCity.text = requireActivity().getString(R.string.txt_rvjobs_location, job.location)
            btnOpenRec.text = job.nameRecruiter
            tvModal.text = requireActivity().getString(R.string.txt_rvjobs_modalidad, job.mode)
            tvType.text = requireActivity().getString(R.string.txt_rvjobs_tipo, job.type)
            tvApplicants.text = if(job.applicants != null)
                requireActivity().getString(R.string.txt_rvjobs_solicitantes_opj, job.applicants!!.size-1) else ""
            tvTime.text = getTime(job.date, job.time)
            tvWage.text = job.wage

            btnContinue.setOnClickListener {
                applyJobViewModel.setNewApplicant(applicantEmail, job)
            }

            btnOpenRec.setOnClickListener {  }
        }
    }

    private fun getTime(dateString: String?, time: String?): String {
        if (dateString != null && time != null) {
            val date = dateString.split("-")
            val hour = time.split(":")

            val year = date[0].toInt()
            val month = date[1].toInt()
            val day = date[2].toInt()

            val hours = hour[0].toInt()
            val minutes = hour[1].toInt()

            val dateOfPost = DateTime(year, month, day, hours, minutes, 0, 0)
            var current = DateTime(DateTimeZone.UTC)

            try {
                current = DateTime.now().withZone(DateTimeZone.forID("America/Mexico_City"))
            } catch (e: Exception){
                e.printStackTrace()
            }

            val timeInDays = Days.daysBetween(dateOfPost, current)
            val timeInHours = Hours.hoursBetween(dateOfPost, current)
            val timeInMinutes = Minutes.minutesBetween(dateOfPost, current)

            return if (timeInMinutes.minutes < 60) requireActivity().getString(R.string.txt_rvjobs_time_min, timeInMinutes.minutes)
            else if (timeInHours.hours < 24) requireActivity().getString(R.string.txt_rvjobs_time_horas, timeInHours.hours)
            else requireActivity().getString(R.string.txt_rvjobs_time_dias, timeInDays.days)
        }
        return ""
    }

}
