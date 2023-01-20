package mx.mauriciogs.ittalent.ui.jobs

import androidx.fragment.app.activityViewModels
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.databinding.FragmentNewJobBinding

class NewJobFragment: BaseFrag<FragmentNewJobBinding>(R.layout.fragment_new_job) {

    private lateinit var mBinding: FragmentNewJobBinding

    private val newJobViewModel: NewJobViewModel by activityViewModels()

    override fun FragmentNewJobBinding.initialize() {
        mBinding = this
        newJobViewModel.getProfile()
        initObservers()
    }

    private fun initObservers() {
        newJobViewModel.jobsUiModelState.observe(viewLifecycleOwner) {
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
        }
    }


}