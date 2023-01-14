package mx.mauriciogs.ittalent.ui.authentication

import androidx.navigation.fragment.findNavController
import com.example.ittalent.R
import com.example.ittalent.databinding.FragmentWelcomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import mx.mauriciogs.ittalent.ui.authentication.adapters.VpWelcomeAdapter
import mx.mauriciogs.ittalent.ui.global.BaseFrag

class WelcomeFragment : BaseFrag<FragmentWelcomeBinding>(R.layout.fragment_welcome) {

    private lateinit var mBinding: FragmentWelcomeBinding

    override fun FragmentWelcomeBinding.initialize() {
        mBinding = this
        initTab()
        initListeners()
    }

    private fun initListeners() {
        with(mBinding) {
            btnContinue.setOnClickListener {
                findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment())
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

    companion object {

        const val TALENT_CARD = 0
        const val RECRUIT_CARD = 1
    }
}
