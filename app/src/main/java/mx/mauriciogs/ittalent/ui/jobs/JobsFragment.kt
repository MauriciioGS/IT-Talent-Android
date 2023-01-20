package mx.mauriciogs.ittalent.ui.jobs

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.ENTERPRISE_R_UT
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
import mx.mauriciogs.ittalent.core.extensions.default
import mx.mauriciogs.ittalent.core.extensions.toast
import mx.mauriciogs.ittalent.databinding.FragmentJobsBinding

class JobsFragment: BaseFrag<FragmentJobsBinding>(R.layout.fragment_jobs) {

    private lateinit var mBinding: FragmentJobsBinding
    private var userType = Int.default()

    override fun FragmentJobsBinding.initialize() {
        mBinding = this
        userType = requireActivity().intent.getIntExtra("userType", 0)
        requireContext().toast("$userType").show()
        initUi()
    }

    private fun initUi() {
        with(mBinding) {
            when(userType) {
                Int.TALENT_UT() -> {
                    toolbar.visibility = View.VISIBLE
                    requireActivity().findViewById<AppBarLayout>(R.id.appbar).apply {
                        this.background = AppCompatResources.getDrawable(requireActivity(), R.drawable.appbar_bg_2)
                    }
                    requireActivity().findViewById<Toolbar>(R.id.toolbarMain).apply {
                        this.background = AppCompatResources.getDrawable(requireActivity(), R.drawable.appbar_bg_2)
                    }
                }
                Int.ENTERPRISE_R_UT() -> {

                }
                else -> {}
            }
        }
    }


}
