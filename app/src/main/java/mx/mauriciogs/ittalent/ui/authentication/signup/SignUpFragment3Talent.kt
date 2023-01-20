package mx.mauriciogs.ittalent.ui.authentication.signup

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment
import mx.mauriciogs.ittalent.ui.authentication.adapters.VpWelcomeAdapter
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmentSignUp3Binding
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.main.MainActivity

@AndroidEntryPoint
class SignUpFragment3Talent : BaseFrag<FragmentSignUp3Binding>(R.layout.fragment_sign_up3) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private lateinit var mBinding: FragmentSignUp3Binding

    override fun FragmentSignUp3Binding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
        initObserver()
        initTab()
    }

    private fun initObserver() {
        signUpViewModel.stopButtonContinue()

        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }

        signUpViewModel.signUpUIModel.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.enableNextStep) {
                Toast.makeText(requireActivity(), "Datos almacenados", Toast.LENGTH_SHORT).show()
                mBinding.viewPager2.currentItem = mBinding.viewPager2.currentItem + 1
            }
            if (it.exception != null) showError(it.exception)
            if (it.success != null) showSignInSuccess(it.success)
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

    private fun showSignInSuccess(userType: Int) {
        Log.d("LOGIN","Usuario loggeado y se creo en firestore")
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            putExtra("userType", userType)
        }
        startActivity(intent).apply { requireActivity().finish() }
    }

    private fun showError(exception: Exception) {
        snackbar(exception.message).showError()
        findNavControllerSafely()?.safeNavigate(SignUpFragment3TalentDirections.actionSignUpFragment3ToSignInFragment())
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
