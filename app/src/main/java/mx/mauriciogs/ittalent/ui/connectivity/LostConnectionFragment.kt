package mx.mauriciogs.ittalent.ui.connectivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.mauriciogs.ittalent.databinding.FragmentLostConnectionBinding
import mx.mauriciogs.ittalent.ui.init.InitViewModel

class LostConnectionFragment : BottomSheetDialogFragment() {

    private val lostConnViewModel : LostConnViewModel by activityViewModels()

    private val initViewModel : InitViewModel by viewModels() {
        InitViewModel.MainVMFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLostConnectionBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener { it ->
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sh ->
                dialog.behavior.peekHeight = sh.height
                sh.parent.parent.requestLayout()
            }
        }
        initViewModel.monitorStateConnection()
        initObserver()
    }

    private fun initObserver() {
        initViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) lostConnViewModel.continueUI()
        }
    }

    companion object {
        fun newInstance() = LostConnectionFragment().apply {
            isCancelable = false
        }
    }
}