package mx.mauriciogs.ittalent.ui.recruitment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.databinding.JobRecruitmentCardBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import org.joda.time.*

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 1
private const val PROCESS_JOB_STAGE3 = 2
private const val PROCESS_JOB_FINISHED = 4

class RecruitmentJobAdapter (private val jobsList: List<Job>, val fragment: Fragment): RecyclerView.Adapter<RecruitmentJobAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: JobRecruitmentCardBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(job: Job) {
            itemBinding.tvJobName.text = job.job
            itemBinding.tvEnterprise.text = job.enterprise
            itemBinding.tvPublisher.text = job.nameRecruiter
            itemBinding.ivVacancies.text =  itemBinding.root.context.getString(R.string.txt_rvjobs_vacantes_old, job.vacancies)
            itemBinding.tvApplicants.text = if(job.applicants != null)
                itemBinding.root.context.getString(R.string.txt_rvjobs_solicitantes_old, job.applicants!!.size-1) else ""
            if (job.vacancies != null && job.applicants != null){
                val rejected = ((job.applicants!!.size-1) - job.vacancies.toInt())
                if (rejected < 0)
                    itemBinding.ivRejected.text = itemBinding.root.context.getString(R.string.txt_rvjobs_rechazados, 0)
                else
                    itemBinding.ivRejected.text = itemBinding.root.context.getString(R.string.txt_rvjobs_rechazados, rejected)
            }
            itemBinding.tvTime.text = getTime(job.date, job.time)

            when(job.status) {
                PROCESS_JOB_STAGE1 -> {
                    itemBinding.btnNextStep.text = itemBinding.root.context.getString(R.string.txt_btn_next_step, "Pasar a la siguiente etapa")
                    itemBinding.ivRejected.visibility = View.GONE
                }
                PROCESS_JOB_STAGE2 -> {
                    itemBinding.btnNextStep.text = itemBinding.root.context.getString(R.string.txt_btn_next_step, "Ver solicitantes")
                }
                PROCESS_JOB_STAGE3 -> {
                    itemBinding.btnNextStep.text = itemBinding.root.context.getString(R.string.txt_btn_next_step, "Ver solicitantes")
                }
                PROCESS_JOB_FINISHED -> { itemBinding.btnNextStep.visibility = View.GONE }
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

                return if (timeInMinutes.minutes < 60) itemBinding.root.context.getString(R.string.txt_rvjobs_time_min, timeInMinutes.minutes)
                else if (timeInHours.hours < 24) itemBinding.root.context.getString(R.string.txt_rvjobs_time_horas, timeInHours.hours)
                else itemBinding.root.context.getString(R.string.txt_rvjobs_time_dias, timeInDays.days)
            }
            return ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(fragment.requireActivity())
        val binding = JobRecruitmentCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.btnNextStep.setOnClickListener {
            if(fragment is RecruitmentFragment) fragment.onClickItem(jobsList[position])
        }
        holder.bind(jobsList[position])
    }

    override fun getItemCount(): Int = jobsList.size

}