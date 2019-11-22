package com.jimmy.actions.featureflags

/**
 * This provider only exists in the release version of the app and defines the baseline of what
 * Features are on or off. TestSettings aren’t exposed in
 * the release version of an app and are always off
 *
 *
 * Notice how you must provide an explicit value for every feature toggle! This is because
 * you never want to accidentally ship an unfinished feature to users. Non gradual rollout
 * of a feature requires an explicit change to the StoreFeatureFlagProvider
 *
 */

class StoreFeatureFlagProvider : FeatureFlagProvider {

    override val priority = LOW_PRIORITY

    @Suppress("ComplexMethod")
    override fun isFeatureEnabled(feature: Feature): Boolean {
        return if (feature is FeatureFlag) {
            // No "else" branch here -> choosing the default
            // option for release must be an explicit choice
            when (feature) {
                FeatureFlag.DARK_MODE -> false
            }
        } else {
            // TestSettings should never be shipped to users
            when (feature as TestSetting) {
                else -> false
            }
        }
    }

    override fun hasFeature(feature: Feature): Boolean = true
}