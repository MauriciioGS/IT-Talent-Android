package mx.mauriciogs.ittalent.ui.postulations

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.core.extensions.showSuccess
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.databinding.FragmentStatusPostulationBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import org.joda.time.*

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 1
private const val PROCESS_JOB_STAGE3 = 2
private const val PROCESS_JOB_FINISHED = 4

class StatusPostulFragment: BaseFrag<FragmentStatusPostulationBinding>(R.layout.fragment_status_postulation) {

    private lateinit var mBinding: FragmentStatusPostulationBinding

    private lateinit var job: Job

    override fun FragmentStatusPostulationBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        initUi()
    }

    private fun initUi() {
        job = StatusPostulFragmentArgs.fromBundle(requireArguments()).job
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

            when (job.status) {
                PROCESS_JOB_STAGE1 -> {
                    tvStatus.text = getString(R.string.txt_postulaciones_stg1)
                    tvStatus.setTextColor(requireContext().getColor(R.color.info))
                }
                PROCESS_JOB_STAGE2 -> {
                    tvStatus.text = getString(R.string.txt_postulaciones_stg2)
                    tvStatus.setTextColor(requireContext().getColor(R.color.warning))
                }
                PROCESS_JOB_STAGE3 -> {
                    tvStatus.text = getString(R.string.txt_postulaciones_stg3)
                    tvStatus.setTextColor(requireContext().getColor(R.color.success))
                }
                PROCESS_JOB_FINISHED -> {
                    tvStatus.text = getString(R.string.txt_postulaciones_stgend)
                    tvStatus.setTextColor(requireContext().getColor(R.color.error))
                }
            }

            btnOpenRec.setOnClickListener {
                MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
                    .setTitle(R.string.txt_contactar)
                    .setMessage(R.string.txt_contactar_desc)
                    .setPositiveButton(R.string.btn_contactar) { dialog, _ ->
                        sendEmail(to = arrayOf(job.emailRecruiter!!), asunto = "Contacto Empleo: ${job.job}")
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_CANCELED -> {
                requireActivity().snackbar("Correo enviado!").showSuccess()
                findNavControllerSafely()?.popBackStack()
            }
            else -> {}
        }
    }

    private fun sendEmail(to: Array<String>, asunto: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, asunto)
        }
        getResult.launch(Intent.createChooser(intent, "Choose an Email client :"))
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
