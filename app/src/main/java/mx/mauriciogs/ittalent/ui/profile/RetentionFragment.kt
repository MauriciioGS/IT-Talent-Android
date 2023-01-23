package mx.mauriciogs.ittalent.ui.profile

import android.content.Intent
import androidx.fragment.app.activityViewModels
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.findNavControllerSafely
import mx.mauriciogs.ittalent.databinding.FragmentRetentionBinding
import mx.mauriciogs.ittalent.ui.init.InitActivity

class RetentionFragment: BaseFrag<FragmentRetentionBinding>(R.layout.fragment_retention) {

    private lateinit var mBinding: FragmentRetentionBinding

    private val myProfileRecViewModel: MyProfileRecViewModel by activityViewModels()

    override fun FragmentRetentionBinding.initialize() {
        mBinding = this
        showToolBar()
        initCloseBntListener(findNavControllerSafely())
        showFloatingActionBtn(show = false)
        initUI()
    }

    private fun initUI() {
        with (mBinding) {
            btnCancel.setOnClickListener { findNavControllerSafely()?.popBackStack() }
            btnContinue.setOnClickListener { myProfileRecViewModel.deleteProfile() }
        }

        myProfileRecViewModel.myProfileModelState.observe(viewLifecycleOwner) {
            if(it.showSuccessDeletion != null)  navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        startActivity(
            Intent(
                requireActivity(),
                InitActivity::class.java
            )
        ).apply { requireActivity().finish() }
    }

}
