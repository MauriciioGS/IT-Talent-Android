package mx.mauriciogs.ittalent.ui.global.extensions

fun Boolean.Companion.yes() = true
fun Boolean.Companion.no() = false
fun Boolean?.orDefault(default: Boolean = false) = this ?: default
