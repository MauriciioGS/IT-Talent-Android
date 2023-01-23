package mx.mauriciogs.ittalent.ui.postulations.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.toast
import mx.mauriciogs.ittalent.databinding.JobForTalentCardBinding
import mx.mauriciogs.ittalent.databinding.JobPostedCardBinding
import mx.mauriciogs.ittalent.databinding.JobProcessTalentCardBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.JobsFragment
import mx.mauriciogs.ittalent.ui.postulations.PostulationsTalFragment
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 2
private const val PROCESS_JOB_STAGE3 = 4
private const val PROCESS_JOB_FINISHED = 4

class PostulTalentAdapter(private val jobsList: List<Job>, val fragment: Fragment): RecyclerView.Adapter<PostulTalentAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: JobProcessTalentCardBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(job: Job) {
            itemBinding.tvJobName.text = job.job
            itemBinding.tvEnterprise.text = job.enterprise
            itemBinding.tvCity.text = job.location
            itemBinding.tvModal.text = itemBinding.root.context.getString(R.string.txt_rvjobs_modalidad, job.mode)
            itemBinding.tvType.text = itemBinding.root.context.getString(R.string.txt_rvjobs_tipo, job.type)
            itemBinding.tvTime.text = getTime(job.date, job.time)
            when (job.status) {
                PROCESS_JOB_STAGE1 -> {
                    itemBinding.ivStatus.text = itemBinding.root.context.getString(R.string.txt_postulaciones_stg1)
                    itemBinding.ivStatus.setTextColor(itemBinding.root.context.getColor(R.color.info))
                }
                PROCESS_JOB_STAGE2 -> {
                    itemBinding.ivStatus.text = itemBinding.root.context.getString(R.string.txt_postulaciones_stg2)
                    itemBinding.ivStatus.setTextColor(itemBinding.root.context.getColor(R.color.warning))
                }
                PROCESS_JOB_STAGE3 -> {
                    itemBinding.ivStatus.text = itemBinding.root.context.getString(R.string.txt_postulaciones_stg3)
                    itemBinding.ivStatus.setTextColor(itemBinding.root.context.getColor(R.color.success))
                }
                PROCESS_JOB_FINISHED -> {
                    itemBinding.ivStatus.text = itemBinding.root.context.getString(R.string.txt_postulaciones_stgend)
                    itemBinding.ivStatus.setTextColor(itemBinding.root.context.getColor(R.color.error))
                }

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
        val binding = JobProcessTalentCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            if(fragment is PostulationsTalFragment) fragment.onClickItem(jobsList[position])
        }
        holder.bind(jobsList[position])
    }

    override fun getItemCount(): Int = jobsList.size

}
