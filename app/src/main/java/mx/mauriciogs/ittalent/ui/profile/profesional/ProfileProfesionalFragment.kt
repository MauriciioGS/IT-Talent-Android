package mx.mauriciogs.ittalent.ui.profile.profesional

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.databinding.FragmentProfesionalProfileBinding
import mx.mauriciogs.ittalent.domain.talent.Experience
import mx.mauriciogs.ittalent.domain.talent.Talent
import mx.mauriciogs.ittalent.ui.profile.profesional.adapter.ExperiencesAdapter

class ProfileProfesionalFragment: BaseFrag<FragmentProfesionalProfileBinding>(R.layout.fragment_profesional_profile) {

    private lateinit var mBinding: FragmentProfesionalProfileBinding

    private lateinit var talentProfile: Talent

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_CANCELED -> { findNavControllerSafely()?.popBackStack() }
            else -> {}
        }
    }

    override fun FragmentProfesionalProfileBinding.initialize() {
        mBinding = this
        showToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        initUi()
    }

    private fun initUi() {
        talentProfile = ProfileProfesionalFragmentArgs.fromBundle(requireArguments()).talentProfile
        with(mBinding) {
            tvName.text = talentProfile.fullName
            tvProfRole.text = talentProfile.profRole
            tvLocation.text = "${talentProfile.city}, ${talentProfile.country}"

            initRecyclerXp(talentProfile.experiences)
            initChips(talentProfile.skills)


            btnContinue.setOnClickListener {
                MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
                    .setTitle(getString(R.string.txt_contactar_tal,
                        talentProfile.fullName?.split(" ")?.get(0) ?: ""
                    ))
                    .setMessage(R.string.txt_contactar_tal_desc)
                    .setPositiveButton(R.string.btn_contactar) { dialog, _ ->
                        sendEmail(to = arrayOf(talentProfile.email!!), asunto = "Contacto para oportunidad laboral")
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

        }
    }

    private fun initRecyclerXp(experiences: List<Experience>?) {
        mBinding.rvXp.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = ExperiencesAdapter(experiences!!, this@ProfileProfesionalFragment)
        }
    }

    private fun initChips(skills: List<String>?) {
        with(mBinding) {
            skills?.forEach { skill ->
                val newChip = Chip(requireActivity(),null, R.style.MyChip)
                newChip.text = skill
                chipGroup.addView(newChip)
            }
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

}