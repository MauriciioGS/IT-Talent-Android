package mx.mauriciogs.ittalent.global

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import mx.mauriciogs.ittalent.main.MainActivity

open class BaseFrag <T : ViewDataBinding>(@LayoutRes private val layoutResId : Int): Fragment()
    //, BaseListView {
{
    private var _binding: T? = null
    private val binding: T get() = _binding!!
    private var progressDialog : Dialog? = null
    protected var errorDialog : Dialog? = null

    val activityInit: AppCompatActivity by lazy {
        requireActivity() as MainActivity
    }

    open fun T.initialize(){}

    open fun T.initializeWithContainer(container: ViewGroup?){}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.initialize()
        binding.initializeWithContainer(container)
        return binding.root
    }

    /*override fun showAlert() {
        try {
            progressDialog = loadingDialog()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    override fun hideAlert() {
        try {
            progressDialog?.let {
                if(it.isShowing)
                    it.cancel()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }*/

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
