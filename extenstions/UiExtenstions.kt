
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.util.*

val nA ="N/A"
val emptyString = ""

fun Context?.navigateToActivity(intent: Intent) {
    this?.startActivity(intent)
}

fun Context?.navigateToActivity(className: Class<*>) {
    this?.startActivity(Intent(this, className))
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun showSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun Context.showSnackBar(view: View, message: String) {
    Snackbar.make(this, view, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.showSnackBar(view: View, message: String, action: String, listener: View.OnClickListener) {
    val snackBar = Snackbar.make(this, view, message, Snackbar.LENGTH_LONG)
    snackBar.setAction(action, listener)
    snackBar.show()
}

fun View.performHapticFeedback() {
    this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

fun Context?.showToast(message : String) {
    this?.let {
        Toast.makeText(it,message, Toast.LENGTH_SHORT).show()
    }
}

fun Context?.showToastLong(message: String) {
    this?.let {
        Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context?.showToast(@StringRes resId: Int) {
    this?.let {
        Toast.makeText(this, it.getString(resId), Toast.LENGTH_SHORT).show()
    }
}

fun EditText.hideKeyboard(activity: Activity?) {
    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(), 0)
}

fun Button.setActive() {
    this.isEnabled = true
}

fun Button.setInactive() {
    this.isEnabled = false
}

fun ImageView.setDrawable(context: Context, @DrawableRes resId: Int) {
    this.setImageDrawable(ContextCompat.getDrawable(context, resId))
}
