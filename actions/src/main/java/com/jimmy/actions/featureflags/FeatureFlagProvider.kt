package com.jimmy.actions.featureflags


/**
 * to provide values during debug, release or testing
 *
 * app needs to be able to read out what value (true/false) a Feature is currently set to.
 * This can be done by requesting one of the FeatureFlagProviders
 *
 * This interface will have several implementations with different priorities
 * attached to it so that they can override each other
 *
 * implementations don’t need to provide a value for every Feature thanks to the hasFeature()
 *
 */
interface FeatureFlagProvider {
    @Priorety
    val priority: Int
    fun isFeatureEnabled(feature: Feature): Boolean
    fun hasFeature(feature: Feature): Boolean
}