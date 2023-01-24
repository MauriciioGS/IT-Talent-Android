package mx.mauriciogs.ittalent.ui.recruitment

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.data.jobs.exception.RecruitmentException
import mx.mauriciogs.ittalent.databinding.FragmentRecruitmentBinding
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 1
private const val PROCESS_JOB_STAGE3 = 2
private const val PROCESS_JOB_FINISHED = 4
private val lostConnBottomSheetTag: String = LostConnectionFragment::class.java.simpleName

class RecruitmentFragment: BaseFrag<FragmentRecruitmentBinding>(R.layout.fragment_recruitment) {

    private lateinit var mBinding: FragmentRecruitmentBinding

    private val recruitmentViewModel: RecruitmentViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    override fun FragmentRecruitmentBinding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
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
        recruitmentViewModel.recruitmentFinishUiModelState.observe(viewLifecycleOwner) {
            if (it.showFinishRecruitment != null && it.job != null){
                showSendEmails(it.showFinishRecruitment, it.job)
                recruitmentViewModel.stopFinishRecruitment()
            }
        }
        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }
    }

    private fun showSendEmails(emails: List<String>, job: Job) {
        MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialog)
            .setTitle(R.string.txt_envia_correos)
            .setMessage(R.string.txt_envia_correos_desc)
            .setPositiveButton(R.string.btn_envia_correo) { dialog, _ ->
                sendEmail(emails.toTypedArray(), asunto = "¡Felicidades has sido seleccionado para ${job.job}!")
                dialog.dismiss()
            }
            .show()
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_CANCELED -> {
                requireActivity().snackbar("Correo enviado!").showSuccess()
            }
            else -> {}
        }
    }

    private fun sendEmail(to: Array<String>, asunto: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, asunto)
        }
        getResult.launch(Intent.createChooser(intent, "Choose an Email client :"))
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
            PROCESS_JOB_STAGE2, PROCESS_JOB_STAGE3 -> {
                findNavControllerSafely()
                    ?.safeNavigate(RecruitmentFragmentDirections.actionPostulationsRecFragmentToOpenApplicantsFragment(job.id!!))
            }
            PROCESS_JOB_FINISHED -> {
                requireActivity().snackbar("NO se pueden ver procesos pasados").showWarning()
            }
        }
    }

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@RecruitmentFragment.childFragmentManager.executePendingTransactions()
        if(!this@RecruitmentFragment.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@RecruitmentFragment.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@RecruitmentFragment.findChildFragmentByTag(
        lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }

}
