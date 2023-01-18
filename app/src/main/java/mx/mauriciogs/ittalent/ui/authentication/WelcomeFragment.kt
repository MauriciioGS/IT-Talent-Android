package mx.mauriciogs.ittalent.ui.authentication

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentWelcomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import mx.mauriciogs.ittalent.ui.authentication.adapters.VpWelcomeAdapter
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.global.BaseFrag
import mx.mauriciogs.ittalent.ui.global.extensions.*
import mx.mauriciogs.ittalent.ui.main.MainViewModel

class WelcomeFragment : BaseFrag<FragmentWelcomeBinding>(R.layout.fragment_welcome) {

    private lateinit var mBinding: FragmentWelcomeBinding
    private var user: Boolean = Boolean.yes()

    private val mainViewModel : MainViewModel by viewModels() {
        MainViewModel.MainVMFactory(requireActivity().application)
    }

    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    override fun FragmentWelcomeBinding.initialize() {
        mBinding = this
        mainViewModel.monitorStateConnection()
        initObserver()
        initTab()
        initListeners()
    }

    private fun initObserver() {
        mainViewModel.isConnected.observe(requireActivity()) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(requireActivity()) { if (it) dismissLostConnDialog() }
    }

    private fun initListeners() {
        with(mBinding) {
            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) { if (tab != null) user = tab.position == 0 }
                override fun onTabUnselected(tab: TabLayout.Tab?) { }
                override fun onTabReselected(tab: TabLayout.Tab?) { }
            })

            btnContinue.setOnClickListener {
                val bundle = bundleOf("user" to user)
                findNavControllerSafely()?.safeNavigateBundle(WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment(), bundle)
            }

            btnSignIn.setOnClickListener {
                findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment())
            }
        }
    }

    private fun initTab() {
        with(mBinding) {
            val adapter = VpWelcomeAdapter(this@WelcomeFragment)
            adapter.addFragment(CardUserType(TALENT_CARD))
            adapter.addFragment(CardUserType(RECRUIT_CARD))
            viewPager2.adapter = adapter

            val titles = arrayOf(resources.getString(R.string.soy_talento), resources.getString(R.string.busco_talento))
            TabLayoutMediator(tabLayout, viewPager2) { tab, position -> tab.text = titles[position] }.attach()
        }
    }

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@WelcomeFragment.childFragmentManager.executePendingTransactions()
        if(!this@WelcomeFragment.findChildFragmentByTag(lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@WelcomeFragment.childFragmentManager, lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@WelcomeFragment.findChildFragmentByTag(lostConnBottomSheetTag)?.asDialogFragment()?.run {
        dismiss()
    }

    companion object {

        const val TALENT_CARD = 0
        const val RECRUIT_CARD = 1

        val lostConnBottomSheetTag: String = LostConnectionFragment::class.java.simpleName

    }
}
