package mx.mauriciogs.ittalent.core.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(text: CharSequence?, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(this, text, duration).apply { show() }