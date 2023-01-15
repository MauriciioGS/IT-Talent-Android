package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.ContextThemeWrapper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentRegisterSkillsBinding
import com.google.android.material.chip.Chip
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.empty


class RegisterSkillsFragment : BaseFrag<FragmentRegisterSkillsBinding>(R.layout.fragment_register_skills) {

    private lateinit var mBinding: FragmentRegisterSkillsBinding

    private lateinit var listHSkills: Array<String>
    private var listUserSkills = mutableListOf<String>()

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun FragmentRegisterSkillsBinding.initialize() {
        mBinding = this
        signUpViewModel.getUserSignUpCredentials()
        initObservers()
        initListeners()
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

    private fun initObservers() {
        signUpViewModel.userSignUp.observe(requireActivity()) {
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

            listHSkills = resources.getStringArray(R.array.hskills)
            actvSearchSkill.setAdapter(ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, listHSkills))
        }
    }


}
