package mx.mauriciogs.ittalent.core

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.loadingDialog
import mx.mauriciogs.ittalent.ui.init.InitActivity
import mx.mauriciogs.ittalent.ui.main.MainActivity

interface BaseListView {
    fun showProgressDialog()
    fun hideProgressDialog()
}

open class BaseFrag <T : ViewDataBinding>(@LayoutRes private val layoutResId : Int): Fragment()
    , BaseListView {

    private var _binding: T? = null
    private val binding: T get() = _binding!!
    private var progressDialog : Dialog? = null
    protected var errorDialog : Dialog? = null

    val activityMain: AppCompatActivity by lazy {
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

    override fun showProgressDialog() {
        try {
            progressDialog = loadingDialog()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    override fun hideProgressDialog() {
        try {
            progressDialog?.let {
                if(it.isShowing)
                    it.dismiss()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun initCloseBntListener(navController: NavController?) {
        val activity = (activityMain as MainActivity)
        activity.binding.ivBtnToolbar.setOnClickListener {
            navController?.popBackStack()
        }
    }

    fun showCollapsingToolBar(show: Boolean = false) {
        val activity = (activityMain as MainActivity)
        if (show) {
            activity.binding.toolbarMain.isVisible = true
            activity.binding.ivBtnToolbar.isVisible = false
        } else {
            activity.binding.ivBtnToolbar.isVisible = true
        }
    }

    fun showNewJob(show: Boolean = true) {
        val activity = (activityMain as MainActivity)
        if (show) {
            /*activity.binding.appbar.background = AppCompatResources.getDrawable(activity, R.drawable.appbar_bg_3)
            activity.binding.toolbarMain.background = AppCompatResources.getDrawable(activity, R.drawable.appbar_bg_3)
            activity.binding.ivBtnToolbar.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.ic_close))*/
            activity.binding.appbar.visibility = View.GONE
            activity.binding.toolbarMain.visibility = View.GONE
            activity.binding.bottomNav.visibility = View.GONE
        } else {
            activity.binding.appbar.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
