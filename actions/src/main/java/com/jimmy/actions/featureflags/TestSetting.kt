package com.jimmy.actions.featureflags

/**
 *
 * Both FeatureFlag and TestSetting are enums so that the Kotlin compiler
 * can force you to handle each case explicitly in a when statement.
 *
 * they are not sealed classes, because we need to be able to enumerate all items,
 * later on, to automatically generate a UI from it.
 * (There is no way to ask a sealed class to list all it’s subclasses
 *
 */
enum class TestSetting(
    override val key: String,
    override val title: String,
    override val explanation: String,
    override val defaultValue: Boolean = false
) : Feature {
    USE_DEVELOP_PORTAL("testsetting.usedevelopportal", "Development portal", "Use developer REST endpoint"),
    DEBUG_LOGGING("testsetting.debuglogging", "Enable logging", "Print all app logging to console", defaultValue = true)
}