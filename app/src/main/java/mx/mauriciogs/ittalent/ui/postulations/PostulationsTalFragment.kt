package mx.mauriciogs.ittalent.ui.postulations

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.core.extensions.showWarning
import mx.mauriciogs.ittalent.core.extensions.snackbar
import mx.mauriciogs.ittalent.core.extensions.toast
import mx.mauriciogs.ittalent.databinding.FragmentPostulationsTalBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.adapters.JobsAdapter
import mx.mauriciogs.ittalent.ui.jobs.adapters.PastJobsAdapter
import mx.mauriciogs.ittalent.ui.postulations.adapters.PostulTalentAdapter

private const val PROCESS_JOB_FINISHED = 4

class PostulationsTalFragment: BaseFrag<FragmentPostulationsTalBinding>(R.layout.fragment_postulations_tal) {

    private lateinit var mBinding: FragmentPostulationsTalBinding

    private val postulationsTalViewModel: PostulationsTalViewModel by activityViewModels()

    private var userEmail = String.empty()

    override fun FragmentPostulationsTalBinding.initialize() {
        mBinding = this
        postulationsTalViewModel.getProfile()
        showToolBar(true)
        initObservers()
    }

    private fun initObservers() {
        postulationsTalViewModel.postTalUiModelState.observe(viewLifecycleOwner){
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) requireActivity().snackbar(it.exception.message).showWarning()
            if (it.setUI != null) initUI(it.setUI)
        }
    }

    private fun initUI(jobs: MutableList<Job>) {
        val aciveJobs = mutableListOf<Job>()
        val finishedJobs = mutableListOf<Job>()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            jobs.forEach { job ->
                if (job.status != PROCESS_JOB_FINISHED) aciveJobs.add(job)
                else finishedJobs.add(job)
            }
            withContext(Dispatchers.Main) {
                if (aciveJobs.isNotEmpty()) initRecyclerActives(aciveJobs)
                if (finishedJobs.isNotEmpty()) initRecyclerPast(finishedJobs)
            }
        }
    }

    private fun initRecyclerActives(jobs: MutableList<Job>) {
        mBinding.noDataAnim.visibility = View.GONE
        mBinding.rvActives.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = PostulTalentAdapter(jobs, this@PostulationsTalFragment)
        }
    }

    private fun initRecyclerPast(jobs: MutableList<Job>) {
        mBinding.noDataAnim2.visibility = View.GONE
        mBinding.rvPast.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = PostulTalentAdapter(jobs, this@PostulationsTalFragment)
        }
    }

    fun onClickItem(item: Job) {
        requireActivity().toast("$item").show()
    }

}
