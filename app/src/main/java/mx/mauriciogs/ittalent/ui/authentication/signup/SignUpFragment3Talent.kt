package mx.mauriciogs.ittalent.ui.authentication.signup

import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.asDialogFragment
import mx.mauriciogs.ittalent.core.extensions.findChildFragmentByTag
import mx.mauriciogs.ittalent.core.extensions.orDefault
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment
import mx.mauriciogs.ittalent.ui.authentication.adapters.VpWelcomeAdapter
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.databinding.FragmentSignUp3Binding
import mx.mauriciogs.ittalent.ui.main.MainViewModel

class SignUpFragment3Talent : BaseFrag<FragmentSignUp3Binding>(R.layout.fragment_sign_up3) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val mainViewModel : MainViewModel by viewModels() {
        MainViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private lateinit var mBinding: FragmentSignUp3Binding

    override fun FragmentSignUp3Binding.initialize() {
        mBinding = this
        mainViewModel.monitorStateConnection()
        initObserver()
        initTab()
    }

    private fun initObserver() {
        signUpViewModel.stopButtonContinue()

        mainViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }

        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) {
            if (it.enableNextStep) {
                Toast.makeText(requireActivity(), "Datos almacenados", Toast.LENGTH_SHORT).show()
                mBinding.viewPager2.currentItem = mBinding.viewPager2.currentItem + 1
            }
        }
    }

    private fun initTab() {
        with(mBinding) {
            val adapter = VpWelcomeAdapter(this@SignUpFragment3Talent)
            adapter.addFragment(RegisterSkillsFragment())
            adapter.addFragment(RegisterXPFragment())
            adapter.addFragment(RegisterProfileFragment())
            viewPager2.adapter = adapter
            viewPager2.isUserInputEnabled = false

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

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@SignUpFragment3Talent.childFragmentManager.executePendingTransactions()
        if(!this@SignUpFragment3Talent.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@SignUpFragment3Talent.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@SignUpFragment3Talent.findChildFragmentByTag(
        WelcomeFragment.lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }
}
