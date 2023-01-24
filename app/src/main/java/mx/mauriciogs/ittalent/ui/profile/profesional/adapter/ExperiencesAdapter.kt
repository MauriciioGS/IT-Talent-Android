package mx.mauriciogs.ittalent.ui.profile.profesional.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.databinding.XpCardTalentBinding
import mx.mauriciogs.ittalent.domain.talent.Experience
import mx.mauriciogs.ittalent.ui.profile.profesional.ProfileProfesionalFragment

class ExperiencesAdapter(private val expList: List<Experience>, val fragment: Fragment): RecyclerView.Adapter<ExperiencesAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: XpCardTalentBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(experience: Experience) {
            itemBinding.tvCargo.text = experience.charge
            itemBinding.tvEnterprise.text = experience.enterprise
            itemBinding.tvModal.text =
                itemBinding.root.context.getString(R.string.txt_rvjobs_tipo, experience.mode)
            itemBinding.tvLocation.text =
                itemBinding.root.context.getString(R.string.txt_rvjobs_tipo, experience.city)
            itemBinding.tvPeriod.text = experience.period
            itemBinding.tvLogros.text = experience.achievements
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(fragment.requireActivity())
        val binding = XpCardTalentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =holder.bind(expList[position])

    override fun getItemCount(): Int = expList.size

}
