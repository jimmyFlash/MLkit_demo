package com.jimmy.actions.featureflags

import android.content.Context
import android.content.SharedPreferences


/**
 * This provider only exists in the debug version of the app and allows
 * to dynamically turn features on or off
 * by keeping a SharedPreferences internally where it automatically stores
 * a value for each Feature using its key.
 */
class RuntimeFeatureFlagProvider(applicationContext: Context) : FeatureFlagProvider {

    private val preferences: SharedPreferences =
        applicationContext.getSharedPreferences("runtime.featureflags", Context.MODE_PRIVATE)

    override val priority = MEDIUM_PRIORTY

    override fun isFeatureEnabled(feature: Feature): Boolean =
        preferences.getBoolean(feature.key, feature.defaultValue)

    override fun hasFeature(feature: Feature): Boolean = true

    fun setFeatureEnabled(feature: Feature, enabled: Boolean) =
        preferences.edit().putBoolean(feature.key, enabled).apply()
}