package mx.mauriciogs.ittalent.ui.authentication.signup

import androidx.appcompat.content.res.AppCompatResources
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentSignUp3Binding
import com.google.android.material.tabs.TabLayoutMediator
import mx.mauriciogs.ittalent.ui.authentication.adapters.VpWelcomeAdapter
import mx.mauriciogs.ittalent.ui.global.BaseFrag

class SignUpFragment3 : BaseFrag<FragmentSignUp3Binding>(R.layout.fragment_sign_up3) {

    private lateinit var mBinding: FragmentSignUp3Binding

    override fun FragmentSignUp3Binding.initialize() {
        mBinding = this
        initTab()
        //initListeners()
    }

    private fun initTab() {
        with(mBinding) {
            val adapter = VpWelcomeAdapter(this@SignUpFragment3)
            adapter.addFragment(RegisterSkillsFragment())
            adapter.addFragment(RegisterSkillsFragment())
            adapter.addFragment(RegisterSkillsFragment())
            viewPager2.adapter = adapter

            val titles = arrayOf(resources.getString(R.string.tab_skills), resources.getString(R.string.tab_xp), resources.getString(R.string.tab_profile))
            val icons = arrayOf(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_skills_selector),
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_work_selector),
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_profile_selector))
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text = titles[position]
                tab.icon = icons[position]
            }.attach()
        }
    }
}