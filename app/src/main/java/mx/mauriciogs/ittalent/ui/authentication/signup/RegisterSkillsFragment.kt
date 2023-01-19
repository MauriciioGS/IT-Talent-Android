package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.core.extensions.showError
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.databinding.FragmentRegisterSkillsBinding
import mx.mauriciogs.ittalent.domain.authentication.UserSignUpCredentials


class RegisterSkillsFragment : BaseFrag<FragmentRegisterSkillsBinding>(R.layout.fragment_register_skills) {

    private lateinit var mBinding: FragmentRegisterSkillsBinding

    private lateinit var listSkills: Array<String>
    private var listUserSkills = mutableListOf<String>()

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun FragmentRegisterSkillsBinding.initialize() {
        mBinding = this
        signUpViewModel.getUserSignUpCredentials()
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        signUpViewModel.stopButtonContinue()
        signUpViewModel.userSignUp.observe(viewLifecycleOwner) {
            initUI(it?: return@observe)
        }
    }

    private fun initUI(userSignUpCredentials: UserSignUpCredentials) {
        with(mBinding) {
            val firstName = userSignUpCredentials.fullName.split(" ")
            tvRole.text = getString(R.string.tv_role, firstName[0])

            val roles = resources.getStringArray(R.array.roles)
            dropdownMenuRol.setText(roles[0], false)
            val levels = resources.getStringArray(R.array.level)
            dropdownMenuLevel.setText(levels[0], false)

            listSkills = resources.getStringArray(R.array.hskills)
            actvSearchSkill.setAdapter(ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, listSkills))
        }
    }

    private fun initListeners() {
        with(mBinding) {
            tilHSkills.setEndIconOnClickListener {
                Toast.makeText(requireContext(), tilHSkills.editText?.text.toString(), Toast.LENGTH_SHORT).show()
            }

            tilHSkills.editText?.setOnEditorActionListener { textView, id, _ ->
                val processed = false
                if (id == EditorInfo.IME_ACTION_DONE) {
                    setChip(textView.text.toString())
                    textView.text = String.empty()
                    val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
                }
                processed
            }

            btnContinue.setOnClickListener {
                checkSkills()
            }
        }
    }

    private fun checkSkills() {
        if (listUserSkills.isEmpty() || listUserSkills.size < MINIMUN_SKILLS) {
            snackbar(R.string.error_skills).showError()
        } else {
            val profRole = mBinding.dropdownMenuRol.text.toString()
            val profLevel = mBinding.dropdownMenuLevel.text.toString()
            signUpViewModel.saveSkills(profRole, profLevel, listUserSkills)
        }
    }

    private fun setChip(skill: String) {
        listUserSkills.add(skill)
        with(mBinding) {
            val newChip = Chip(requireActivity(),null, R.style.MyChip)
            newChip.text = skill
            newChip.isCloseIconVisible = true

            newChip.setOnCloseIconClickListener {
                chipGroup.removeView(newChip)
                listUserSkills.remove(newChip.text)
            }

            chipGroup.addView(newChip)
        }
    }

    companion object {

        const val MINIMUN_SKILLS = 2
    }

}
