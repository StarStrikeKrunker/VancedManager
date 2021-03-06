package com.vanced.manager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.SharedPreferences
import android.widget.RadioGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.radiobutton.MaterialRadioButton
import com.vanced.manager.R
import com.vanced.manager.utils.InternetTools.baseUrl
import com.vanced.manager.utils.InternetTools.loadJson
import com.vanced.manager.utils.ThemeHelper.accentColor
import com.vanced.manager.utils.ThemeHelper.defAccentColor
import java.util.*

object Extensions {

    fun RadioGroup.getCheckedButtonTag(): String? {
        return findViewById<MaterialRadioButton>(checkedRadioButtonId)?.tag?.toString()
    }

    fun DialogFragment.show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, "")
    }

    suspend fun Activity.fetchData() {
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.home_refresh)
        setRefreshing(true, refreshLayout)
        loadJson(this)
        setRefreshing(false, refreshLayout)
    }

    fun Activity.setRefreshing(isRefreshing: Boolean) {
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.home_refresh)
        if (refreshLayout != null) {
            refreshLayout.isRefreshing = isRefreshing
        }
    }

    fun Activity.setRefreshing(isRefreshing: Boolean, refreshLayout: SwipeRefreshLayout?) {
        if (refreshLayout != null) {
            refreshLayout.isRefreshing = isRefreshing
        }
    }

    fun Context.getDefaultPrefs(): SharedPreferences = getDefaultSharedPreferences(this)

    //Not sure how much this can affect performance
    //but if anyone can improve this even slightly,
    //feel free to open a PR
    fun List<String>.convertToAppVersions(): List<String> {
        val versionsModel = arrayListOf("latest")
        for (i in reversed().indices) {
            versionsModel.add(this[i])
        }
        return versionsModel
    }

    fun String.convertToAppTheme(context: Context): String {
        return context.getString(R.string.light_plus_other, this.capitalize(Locale.ROOT))
    }

    fun String.getLatestAppVersion(versions: List<String>): String {
        return if (this == "latest") versions.reversed()[0] else this
    }

    fun SharedPreferences.getInstallUrl() = getString("install_url", baseUrl)

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext
        } else {
            null
        }
    }

    fun Int.toHex(): String = java.lang.String.format("#%06X", 0xFFFFFF and this)

    //Material team decided to keep their LinearProgressIndicator final
    //At least extension methods exist
    fun LinearProgressIndicator.applyAccent() {
        with(accentColor.value ?: context.getDefaultPrefs().getInt("manager_accent", defAccentColor)) {
            setIndicatorColor(this)
            trackColor = ColorUtils.setAlphaComponent(this, 70)
        }
    }

    fun MaterialAlertDialogBuilder.applyAccent() {
        with(accentColor.value ?: context.getDefaultPrefs().getInt("manager_accent", defAccentColor)) {
            show().apply {
                getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(this@with)
                getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(this@with)
                getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(this@with)
            }
        }
    }

}