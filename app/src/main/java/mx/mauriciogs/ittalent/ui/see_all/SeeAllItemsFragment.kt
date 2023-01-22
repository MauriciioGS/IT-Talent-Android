package mx.mauriciogs.ittalent.ui.see_all

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmenSeeAllItemsBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.adapters.JobsAdapter
import mx.mauriciogs.ittalent.ui.jobs.adapters.PastJobsAdapter

class SeeAllItemsFragment: BaseFrag<FragmenSeeAllItemsBinding>(R.layout.fragmen_see_all_items) {

    private lateinit var mBinding: FragmenSeeAllItemsBinding

    private val seeAllItemsViewModel: SeeAllItemsViewModel by activityViewModels()

    override fun FragmenSeeAllItemsBinding.initialize() {
        mBinding = this
        seeAllItemsViewModel.getProfile()
        showCollapsingToolBar()
        showFloatingActionBtn(show = false)
        initCloseBntListener(findNavControllerSafely())
        initObservers()
    }

    private fun initObservers() {
        seeAllItemsViewModel.seeAllUiModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.setUI != null) getAdapterType()
            if (it.exception != null) showError(it.exception)
            if (it.showSuccess != null) initRecycler()
        }
    }

    private fun showError(exception: Exception) {
        requireActivity().snackbar(exception.message).showError()
    }

    private fun getAdapterType() {
        if (arguments != null) {
            when(requireArguments().getInt(ADAPTER_KEY, 0)) {
                ACTIVE_JOBS_KEY -> {
                    mBinding.tvHeader.text = getText(R.string.subheader_jobs_rec)
                    seeAllItemsViewModel.getJobsByRecruiter(isActives = Boolean.yes())
                }
                PAST_JOBS_KEY -> {
                    mBinding.tvHeader.text = getText(R.string.subheader_jobs_rec2)
                    seeAllItemsViewModel.getJobsByRecruiter(isActives = Boolean.no())
                }
            }
        }
    }


    private fun initRecycler() {
        when(requireArguments().getInt(ADAPTER_KEY, 0)) {
            ACTIVE_JOBS_KEY -> {
                mBinding.rvItems.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = JobsAdapter(seeAllItemsViewModel.myPosts, this@SeeAllItemsFragment)
                }
            }
            PAST_JOBS_KEY -> {
                mBinding.rvItems.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = PastJobsAdapter(seeAllItemsViewModel.myPosts, this@SeeAllItemsFragment)
                }
            }
        }


    }

    fun onClickItem(item: Job) {
        requireActivity().toast("$item").show()
    }

    companion object {

        private const val ADAPTER_KEY = "adapter"
        private const val ACTIVE_JOBS_KEY = 0
        private const val PAST_JOBS_KEY = 1
    }
}