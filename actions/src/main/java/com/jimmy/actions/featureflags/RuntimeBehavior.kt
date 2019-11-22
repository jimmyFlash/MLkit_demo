package com.jimmy.actions.featureflags

import android.content.Context
import android.support.annotation.VisibleForTesting
import java.util.concurrent.CopyOnWriteArrayList

/**
 * to easily consume feature flags
 *
 * RuntimeBehavior links all FeatureFlagProviders together and exposes
 * the API that should be used from within the application
 *
 */

object RuntimeBehavior {

    @VisibleForTesting
    internal val providers = CopyOnWriteArrayList<FeatureFlagProvider>()

    /**
     *
     * how it takes all FeatureFlagProviders, removes those that don’t provide a
     * value for the Feature and then takes the value of the highest priority provider.
     * If no one provides a value, the default value is returned
     *
     */
    @JvmStatic
    fun isFeatureEnabled(feature: Feature): Boolean {
        return providers.filter { it.hasFeature(feature) }
            .sortedBy(FeatureFlagProvider::priority)
            .firstOrNull()
            ?.isFeatureEnabled(feature)
            ?: feature.defaultValue
    }

    @JvmStatic
    fun addProvider(provider: FeatureFlagProvider) = providers.add(provider)

    /**
     * Whenever the RuntimeBehavior is initialized, it will initialize all providers
     */
    @JvmStatic
    fun initialize(context: Context, isDebugBuild: Boolean) {
        if (isDebugBuild) {
            /*
            debug builds, usually only the RuntimeFeatureFlagProvider is enabled so feature
            flags can be toggled from the test settings screen
             */
            val runtimeFeatureFlagProvider = RuntimeFeatureFlagProvider(context)
            addProvider(RuntimeFeatureFlagProvider(context))
            addProvider(TestFeatureFlagProvider)

           /* if (runtimeFeatureFlagProvider.isFeatureEnabled(TestSetting.DEBUG_FIREBASE)) {
                addProvider(FirebaseFeatureFlagProvider(true))
            }*/
        } else {
            addProvider(StoreFeatureFlagProvider())
           // addProvider(FirebaseFeatureFlagProvider(false))
        }
    }
}
/*
callback sample

if (RuntimeBehavior.isFeatureEnabled(FeatureFlag.DARK_MODE)) {
  // set dark theme
} else {
  // set light them
}

 */