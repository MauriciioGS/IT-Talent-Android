package mx.mauriciogs.ittalent.ui.talent

import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.core.extensions.*
import mx.mauriciogs.ittalent.data.talent.exceptions.TalentExceptionHandler
import mx.mauriciogs.ittalent.databinding.FragmentTalentBinding
import mx.mauriciogs.ittalent.domain.talent.Talent
import mx.mauriciogs.ittalent.ui.connectivity.LostConnViewModel
import mx.mauriciogs.ittalent.ui.connectivity.LostConnectionFragment
import mx.mauriciogs.ittalent.ui.init.InitViewModel
import mx.mauriciogs.ittalent.ui.talent.adapters.TalentAdapter
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment

private val lostConnBottomSheetTag: String = LostConnectionFragment::class.java.simpleName

class TalentFragment: BaseFrag<FragmentTalentBinding>(R.layout.fragment_talent){

    private lateinit var mBinding: FragmentTalentBinding

    private val talentViewModel : TalentViewModel by activityViewModels()
    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }
    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private var talent = mutableListOf<Talent>()

    private var listAdaper = TalentAdapter(talent, this)

    override fun FragmentTalentBinding.initialize() {
        mBinding = this
        initViewModel.monitorStateConnection()
        showFloatingActionBtn(show = false)
        initObservers()
        talentViewModel.getTalent()
    }

    private fun initObservers() {
        talentViewModel.talentUiModelState.observe(viewLifecycleOwner) {
            if(it.showProgress) showProgressDialog() else hideProgressDialog()
            if (it.exception != null) showError(it.exception)
            if (it.showSuccess != null) initUI()
            if (it.showSuccessFiler != null) showTalent(it.showSuccessFiler)
        }
        initViewModel.isConnected.observe(viewLifecycleOwner) { isConnected -> if (!isConnected) openLostConnDialog() }
        lostConnViewModel.isUiEnabled.observe(viewLifecycleOwner) { if (it) dismissLostConnDialog() }
    }

    private fun initUI() {
        with(mBinding) {
            val roles = resources.getStringArray(R.array.roles)
            dropdownMenuModalidad.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, roles))

            dropdownMenuModalidad.setOnItemClickListener { adapterView, view, i, l ->
                val selected = adapterView.adapter.getItem(i).toString()
                tvHeader.text = selected
                talentViewModel.filterByRole(selected)
            }

            rvTalent.apply {
                visibility = View.VISIBLE
                layoutManager = GridLayoutManager(requireActivity(), 2)
                adapter = listAdaper
            }
        }

    }

    private fun showTalent(talentList: MutableList<Talent>) {
        listAdaper = TalentAdapter(talentList, this)
        mBinding.rvTalent.adapter = listAdaper
    }

    fun onClickItem(item: Talent) {
        findNavControllerSafely()
            ?.safeNavigate(TalentFragmentDirections.actionTalentFragmentToProfileProfesionalFragment(item))
    }

    private fun showError(exception: Exception) {
        when(exception) {
            is TalentExceptionHandler.TalentEmpyList -> {
                listAdaper = TalentAdapter(talent, this)
                mBinding.rvTalent.adapter = listAdaper
                requireActivity().snackbar(exception.message).showWarning()
            }
            else -> requireActivity().snackbar(exception.message).showError()
        }
    }

    private fun openLostConnDialog() = LostConnectionFragment.newInstance().run {
        this@TalentFragment.childFragmentManager.executePendingTransactions()
        if(!this@TalentFragment.findChildFragmentByTag(WelcomeFragment.lostConnBottomSheetTag)?.isAdded.orDefault())
            show(this@TalentFragment.childFragmentManager, WelcomeFragment.lostConnBottomSheetTag)
    }

    private fun dismissLostConnDialog() = this@TalentFragment.findChildFragmentByTag(
        lostConnBottomSheetTag
    )?.asDialogFragment()?.run {
        dismiss()
    }

}
