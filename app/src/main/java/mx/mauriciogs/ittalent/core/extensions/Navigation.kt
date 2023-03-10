package mx.mauriciogs.ittalent.core.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController


fun Fragment.findNavControllerSafely() : NavController? {
    return if (isAdded) findNavController() else null
}

fun Fragment.navigate(directions: NavDirections) {
    val controller = findNavController()
    val currentDestination = (controller.currentDestination as? FragmentNavigator.Destination)?.className
        ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
    if (currentDestination == this.javaClass.name) controller.navigate(directions)
}

fun NavController.safeNavigate(direction: NavDirections) {
    //Log.d("CurrentFrag", "Curr: $currentDestination")
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun NavController.safeNavigateBundle(direction: NavDirections, bundle: Bundle) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction.actionId, bundle) }
}