package mx.mauriciogs.ittalent.core.extensions

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> Fragment.viewBinding(initialise: () -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

        private var binding: T? = null
        private var viewLifecycleOwner: LifecycleOwner? = null

        init {
            liveDataObserve(this@viewBinding.viewLifecycleOwnerLiveData) { newLifecycleOwner ->
                viewLifecycleOwner?.lifecycle?.removeObserver(this)
                viewLifecycleOwner = newLifecycleOwner.also { it?.lifecycle?.addObserver(this) }
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            binding = null
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T = this.binding ?: initialise().also { this.binding = it }
    }

fun <T : Any, L : LiveData<T>> LifecycleOwner.liveDataObserve(liveData: L, body: (T?) -> Unit) =
    liveData.observe(this, Observer(body))

fun Fragment.findChildFragmentByTag(tag: String) = childFragmentManager.findFragmentByTag(tag)
fun Fragment.asDialogFragment() = this as? DialogFragment
