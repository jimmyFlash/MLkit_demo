package com.jimmy.actions

import com.jimmy.actions.featureflags.FeatureFlag
import com.jimmy.actions.featureflags.TestFeatureFlagProvider
import org.junit.After
import org.junit.Test

@Test
fun withFeatureFlags() {
    TestFeatureFlagProvider.setFeatureEnabled(FeatureFlag.DARK_MODE, true)

    // do test here
}

@After
fun tearDown() {
    TestFeatureFlagProvider.clearFeatures()
}