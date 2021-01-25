@file:Suppress("unused")

package kr.dksin.paginggallerypicker.initializer

import android.content.Context
import androidx.startup.Initializer
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class LoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
//                return BuildConfig.DEBUG
//                return false
            }
        })
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}