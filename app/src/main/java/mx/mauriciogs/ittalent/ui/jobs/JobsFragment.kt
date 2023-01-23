package mx.mauriciogs.ittalent.ui.jobs

import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.databinding.FragmentJobsBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.adapters.JobsAdapter
import mx.mauriciogs.ittalent.ui.jobs.adapters.JobsTalentAdapter
import mx.mauriciogs.ittalent.ui.jobs.adapters.PastJobsAdapter

private const val ACTIVE_JOBS_KEY = 0
private const val PAST_JOBS_KEY = 1

class JobsFragment: BaseFrag<FragmentJobsBinding>(R.layout.fragment_jobs) {

    private lateinit var mBinding: FragmentJobsBinding

    private val jobsViewModel: JobsViewModel by activityViewModels()

    private var userType = Int.default()
    private var listUserSkills = mutableListOf<String>()

    private var jobsTalent = mutableListOf<Job>()

    private var listAdaper = JobsTalentAdapter(jobsTalent, this)

    override fun FragmentJobsBinding.initialize() {
        mBinding = this
        showToolBar(true)
        userType = requireActivity().intent.getIntExtra("userType", 0)
        //userType = 2
        requireContext().toast("$userType").show()
        if (userType == Int.TALENT_UT()) jobsViewModel.getProfileTalent()
        else jobsViewModel.getProfile()
        initListeners()
        initObservers()
        //initAdapter()

    }

    private fun initObservers() {
        jobsViewModel.jobsUiModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if(it.exception != null) showError(it.exception)
            if (it.setUI != null) initUi()
            if(it.showSuccessActiveJobs != null) initRecyclerActives(it.showSuccessActiveJobs)
            if (it.showSuccessPastJobs != null) initRecyclerPast(it.showSuccessPastJobs)
        }

        jobsViewModel.jobsUiTalentModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if(it.exception != null) showError(it.exception)
            if (it.setUI != null) initUi(it.setUI)
            if(it.showSuccessNewFilter != null) initRecyclerActives(it.showSuccessNewFilter)
        }
    }

    private fun initUi(jobsFiltered: MutableList<Job>? = null) {
        with(mBinding) {
            when(userType) {
                Int.TALENT_UT() -> {
                    initRecyclerJobsForTalent()
                    toolbar.visibility = View.VISIBLE
                    requireActivity().findViewById<AppBarLayout>(R.id.appbar).apply {
                        this.background = AppCompatResources.getDrawable(requireActivity(), R.drawable.appbar_bg_2)
                    }
                    requireActivity().findViewById<Toolbar>(R.id.toolbarMain).apply {
                        this.background = AppCompatResources.getDrawable(requireActivity(), R.drawable.appbar_bg_2)
                    }
                    tvHeader.text = getText(R.string.header_jobs_tal)
                    tvActives.text = jobsViewModel.profile.profRole
                    clRecycler2.visibility = View.GONE
                    setChips()
                    if (jobsFiltered != null && jobsFiltered.isNotEmpty()){
                        mBinding.noDataAnim.visibility = View.GONE
                        listAdaper = JobsTalentAdapter(jobsFiltered, this@JobsFragment)
                        rvActives.adapter = listAdaper
                    }

                }
                Int.ENTERPRISE_R_UT() -> {
                    toolbar.visibility = View.GONE
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

    private fun initListeners() {
        with(mBinding) {
            btnOpenAll.setOnClickListener {
                findNavControllerSafely()
                    ?.safeNavigate(JobsFragmentDirections.actionJobsFragmentToSeeJobsFragment(ACTIVE_JOBS_KEY))
            }
            btnOpenAll2.setOnClickListener {
                findNavControllerSafely()
                    ?.safeNavigate(JobsFragmentDirections.actionJobsFragmentToSeeJobsFragment(PAST_JOBS_KEY))
            }
        }
    }

    private fun initRecyclerActives(jobs: MutableList<Job>) {
        mBinding.noDataAnim.visibility = View.GONE
        mBinding.btnOpenAll.visibility = View.VISIBLE
        mBinding.rvActives.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = JobsAdapter(jobs, this@JobsFragment)
        }
    }

    private fun initRecyclerPast(jobs: MutableList<Job>) {
        mBinding.noDataAnim2.visibility = View.GONE
        mBinding.btnOpenAll2.visibility = View.VISIBLE
        mBinding.rvPast.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = PastJobsAdapter(jobs, this@JobsFragment)
        }
    }

    private fun initRecyclerJobsForTalent() {
        mBinding.rvActives.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = listAdaper
        }
    }

    private fun setChips() {
        with(mBinding) {
            val newChip = Chip(requireActivity(),null, R.style.MyChip)
            newChip.text = jobsViewModel.profile.profRole
            newChip.isCloseIconVisible = false
            chipGroup.addView(newChip)

            /*jobsViewModel.profile.skills?.forEach{ skill ->
                listUserSkills.add(skill)
                val newChip = Chip(requireActivity(),null, R.style.MyChip)
                newChip.text = skill
                newChip.isCloseIconVisible = true
                newChip.setOnCloseIconClickListener {
                    chipGroup.removeView(newChip)
                    listUserSkills.remove(newChip.text)
                }
                chipGroup.addView(newChip)
            }*/
        }
    }

    fun onClickItem(item: Job) {
        requireActivity().toast("$item").show()
    }

    fun onClickItemTalent(item: Job) {
        findNavControllerSafely()?.safeNavigate(
            JobsFragmentDirections.actionJobsFragmentToApplyJobFragment(item, jobsViewModel.profile.email!!))
    }

    private fun showError(exception: Exception) {
        when(exception){
            is JobsException.EmptyListOfPastJobs -> {
                mBinding.rvPast.visibility = View.GONE
                mBinding.noDataAnim2.visibility = View.VISIBLE
                requireActivity().snackbar(exception.message).showError()
            }
            is JobsException.EmptyListOfAciveJobs -> {
                mBinding.rvActives.visibility = View.GONE
                mBinding.noDataAnim.visibility = View.VISIBLE
            }
            is JobsException.EmptyListOfFilteredJobs -> {
                listAdaper = JobsTalentAdapter(jobsTalent, this)
                mBinding.rvActives.adapter = listAdaper
                requireActivity().snackbar(exception.message).showWarning()
            }
            else -> requireActivity().snackbar(exception.message).showError()
        }
    }

}
