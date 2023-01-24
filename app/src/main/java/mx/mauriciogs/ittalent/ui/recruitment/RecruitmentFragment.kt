package mx.mauriciogs.ittalent.ui.recruitment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.data.jobs.exception.RecruitmentException
import mx.mauriciogs.ittalent.databinding.FragmentRecruitmentBinding
import mx.mauriciogs.ittalent.domain.jobs.Job

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 1
private const val PROCESS_JOB_STAGE3 = 2
private const val PROCESS_JOB_FINISHED = 4

class RecruitmentFragment: BaseFrag<FragmentRecruitmentBinding>(R.layout.fragment_recruitment) {

    private lateinit var mBinding: FragmentRecruitmentBinding

    private val recruitmentViewModel: RecruitmentViewModel by activityViewModels()

    override fun FragmentRecruitmentBinding.initialize() {
        mBinding = this
        showToolBar(true)
        initObservers()
        showFloatingActionBtn(show = false)
        recruitmentViewModel.getProfile()
    }

    private fun initObservers() {
        recruitmentViewModel.recruitmentUiModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if(it.exception != null) showError(it.exception)
            if(it.showSuccessStepOne != null) initRecyclerStepOne(it.showSuccessStepOne)
            if(it.showSuccessStepTwo != null) initRecyclerStepTwo(it.showSuccessStepTwo)
            if(it.showSuccessStepThree != null) initRecyclerStepThree(it.showSuccessStepThree)
            if(it.showSuccessStepFour != null) initRecyclerStepFour(it.showSuccessStepFour)
            if (it.showSuccessUpdate != null) {
                requireActivity().snackbar("Proceso actualizado!").showSuccess()
                recruitmentViewModel.getProfile()
            }
        }
    }

    private fun initRecyclerStepOne(jobs: MutableList<Job>) {
        mBinding.rvEtapa1.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = RecruitmentJobAdapter(jobs, this@RecruitmentFragment)
        }
    }

    private fun initRecyclerStepTwo(jobs: MutableList<Job>) {
        mBinding.rvEtapa2.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = RecruitmentJobAdapter(jobs, this@RecruitmentFragment)
        }
    }

    private fun initRecyclerStepThree(jobs: MutableList<Job>) {
        mBinding.rvEtapa3.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = RecruitmentJobAdapter(jobs, this@RecruitmentFragment)
        }
    }

    private fun initRecyclerStepFour(jobs: MutableList<Job>) {
        mBinding.rvEtapa4.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(requireActivity()).apply { orientation = RecyclerView.HORIZONTAL }
            adapter = RecruitmentJobAdapter(jobs, this@RecruitmentFragment)
        }
    }

    private fun showError(exception: Exception) {
        when(exception){
            is RecruitmentException.EmptyListStage1 -> {
                mBinding.noDataAnim.visibility = View.VISIBLE
            }
            is RecruitmentException.EmptyListStage2 -> {
                mBinding.noDataAnim2.visibility = View.VISIBLE
            }
            is RecruitmentException.EmptyListStage3 -> {
                mBinding.noDataAnim3.visibility = View.VISIBLE
            }
            is RecruitmentException.EmptyListStage4 -> {
                mBinding.noDataAnim4.visibility = View.VISIBLE
            }
            else -> requireActivity().snackbar(exception.message).showError()
        }
    }

    fun onClickItem(job: Job) {
        when(job.status) {
            PROCESS_JOB_STAGE1 -> {
                MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
                    .setTitle(R.string.txt_header_next_step)
                    .setMessage(R.string.txt_next_step_desc)
                    .setPositiveButton(R.string.btn_continue) { dialog, _ ->
                        recruitmentViewModel.setNextStep(job)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.btn_regresar) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            PROCESS_JOB_STAGE2 -> {
                findNavControllerSafely()
                    ?.safeNavigate(RecruitmentFragmentDirections.actionPostulationsRecFragmentToOpenApplicantsFragment(job.id!!))
            }
            PROCESS_JOB_STAGE3 -> {
                findNavControllerSafely()
                    ?.safeNavigate(RecruitmentFragmentDirections.actionPostulationsRecFragmentToOpenApplicantsFragment(job.id!!))
            }
            PROCESS_JOB_FINISHED -> {
                requireActivity().snackbar("NO se pueden ver procesos pasados").showWarning()
            }
        }
    }

}
