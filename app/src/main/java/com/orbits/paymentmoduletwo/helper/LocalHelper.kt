package com.orbits.paymentmoduletwo.helper

import android.content.Context
import android.content.res.Configuration
import com.orbits.paymentmoduletwo.helper.PrefUtils.getAppConfig
import com.orbits.paymentmoduletwo.helper.PrefUtils.setAppConfig
import com.orbits.paymentmoduletwo.helper.helper_model.AppConfigModel
import java.util.Locale

object LocaleHelper {

    fun changeLanguage(context: Context) {
        if (context.getAppConfig()?.lang == "en") {
            setLocale(context , "ar")
        } else {
            setLocale(context, "en")
        }
    }


    // the method is used to set the language at runtime
    fun setLocale(context: Context, language: String): Context {
        setInPref(context, language)

        // updating the language for devices above android nougat
        return updateResources(context, language)
        // for devices having lower version of android os
    }

    private fun setInPref(context: Context, language: String) {
        val model = context.getAppConfig()
        context.setAppConfig(AppConfigModel(lang = language, cartBadgeCount = model?.cartBadgeCount ?: ""))
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    private fun updateResources(context: Context, language: String): Context {
        /*val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)*/

        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}