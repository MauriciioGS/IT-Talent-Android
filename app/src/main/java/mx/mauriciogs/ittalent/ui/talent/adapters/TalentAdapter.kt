package mx.mauriciogs.ittalent.ui.talent.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.databinding.TalentCardBinding
import mx.mauriciogs.ittalent.domain.talent.Talent
import mx.mauriciogs.ittalent.ui.talent.TalentFragment

class TalentAdapter(private val talentList: List<Talent>, val fragment: Fragment): RecyclerView.Adapter<TalentAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: TalentCardBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: Talent) {
            val shortName = "${user.fullName?.split(" ")?.get(0)} ${user.fullName?.split(" ")?.get(1)}"
            itemBinding.tvName.text = shortName
            itemBinding.tvRol.text = user.profRole
            itemBinding.tvLocation.text = "${user.city}, ${user.country}"
            itemBinding.tvXp.text = itemBinding.root.context.getString(R.string.txt_years_experience,
                user.experiences?.get(0)?.yearsXp ?: 0
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(fragment.requireActivity())
        val binding = TalentCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            if(fragment is TalentFragment) fragment.onClickItem(talentList[position])
        }
        holder.bind(talentList[position])
    }

    override fun getItemCount(): Int = talentList.size

}
