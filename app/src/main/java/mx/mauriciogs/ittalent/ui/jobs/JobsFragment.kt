package mx.mauriciogs.ittalent.ui.jobs

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.databinding.FragmentJobsBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.adapters.JobsAdapter

class JobsFragment: BaseFrag<FragmentJobsBinding>(R.layout.fragment_jobs) {

    private lateinit var mBinding: FragmentJobsBinding

    private val jobsViewModel: JobsViewModel by activityViewModels()

    private var userType = Int.default()

    override fun FragmentJobsBinding.initialize() {
        mBinding = this
        showCollapsingToolBar(true)
        //userType = requireActivity().intent.getIntExtra("userType", 0)
        userType = 2
//        requireContext().toast("$userType").show()
        jobsViewModel.getProfile()
        initListeners()
        initObservers()
        //initAdapter()

    }

    private fun initObservers() {
        jobsViewModel.jobsUiModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if(it.exception != null) showError(it.exception)
            if (it.setUI != null) initUi()
            if(it.showSuccess != null) initRecycler(it.showSuccess)
        }
    }

    private fun showError(exception: Exception) {
        if (exception is JobsException.EmptyListOfJobs) {
            mBinding.rvActives.isVisible = false
            mBinding.noDataAnim.visibility = View.VISIBLE
        }
        snackbar(exception.message).showError()
    }

    private fun initListeners() {
        with(mBinding) {
            btnOpenAll.setOnClickListener {

            }
            btnOpenAll2.setOnClickListener {

            }
        }
    }

    private fun initRecycler(jobs: MutableList<Job>) {
        mBinding.noDataAnim.visibility = View.GONE
        mBinding.btnOpenAll.visibility = View.VISIBLE
        mBinding.btnOpenAll2.visibility = View.VISIBLE
        mBinding.rvActives.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = JobsAdapter(jobs, this@JobsFragment)
        }
    }

    fun onClickItem(item: Job) {
        requireActivity().toast("$item").show()
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
                    tvHeader.text = getText(R.string.header_jobs_tal)
                    //tvActives.text = getText(R.string.subheader_jobs_rec)
                    //tvPast.text = getText(R.string.subheader_jobs_rec2)
                }
                Int.ENTERPRISE_R_UT() -> {
                    tvHeader.text = getText(R.string.header_jobs_rec)
                    tvActives.text = getText(R.string.subheader_jobs_rec)
                    tvPast.text = getText(R.string.subheader_jobs_rec2)
                    showFloatingActionBtn(findNavControllerSafely())
                    jobsViewModel.getMyJobPosts()
                }
                else -> {}
            }
        }
    }

}
