package com.jimmy.actions.featureflags

/**
 * FeatureFlag is on by default so that it is immediately visible in developer builds
 *
 * Both FeatureFlag and TestSetting are enums so that the Kotlin compiler
 * can force you to handle each case explicitly in a when statement.
 *
 * they are not sealed classes, because we need to be able to enumerate all items,
 * later on, to automatically generate a UI from it.
 * (There is no way to ask a sealed class to list all it’s subclasses
 *
 */
enum class FeatureFlag(
    override val key: String,
    override val title: String,
    override val explanation: String,
    override val defaultValue: Boolean = true
) : Feature {
    DARK_MODE("feature.darkmode", "Dark theme", "Enabled dark mode")
}