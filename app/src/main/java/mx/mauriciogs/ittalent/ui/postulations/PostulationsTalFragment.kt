package mx.mauriciogs.ittalent.ui.postulations

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.databinding.FragmentPostulationsTalBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.postulations.adapters.PostulPastTalentAdapter
import mx.mauriciogs.ittalent.ui.postulations.adapters.PostulTalentAdapter
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment

private const val PROCESS_JOB_FINISHED = 4
private val lostConnBottomSheetTag: String = LostConnectionFragment::class.java.simpleName

class PostulationsTalFragment: BaseFrag<FragmentPostulationsTalBinding>(R.layout.fragment_postulations_tal) {

    private lateinit var mBinding: FragmentPostulationsTalBinding

    private val postulationsTalViewModel: PostulationsTalViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private var userEmail = String.empty()

    override fun FragmentPostulationsTalBinding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
        showToolBar(true)
        initObservers()
        postulationsTalViewModel.getProfile()
    }

    private fun initObservers() {
        postulationsTalViewModel.postTalUiModelState.observe(viewLifecycleOwner){
            if (it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) requireActivity().snackbar(it.exception.message).showWarning()
            if (it.setUI != null) initUI(it.setUI)
        }
        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }
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
        println(jobs)
        mBinding.noDataAnim2.visibility = View.GONE
        mBinding.rvPast.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = PostulPastTalentAdapter(jobs, this@PostulationsTalFragment)
        }
    }

    fun onClickItem(item: Job) {
        findNavControllerSafely()
            ?.safeNavigate(PostulationsTalFragmentDirections.actionPostulationsTalFragmentToStatusPostulFragment(item))
    }

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@PostulationsTalFragment.childFragmentManager.executePendingTransactions()
        if(!this@PostulationsTalFragment.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@PostulationsTalFragment.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@PostulationsTalFragment.findChildFragmentByTag(
        lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }

}
