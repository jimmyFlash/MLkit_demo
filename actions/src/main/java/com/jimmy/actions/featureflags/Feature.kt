package com.jimmy.actions.featureflags
/**
 * one-line definition of FeatureFlags or TestSettings
 *
 * key to reference it on your remote feature flagging tool.
 * A title
 * description to help understand what it is all about.
 * And optionally a default value
 *
 */
interface Feature {
    val key: String
    val title: String
    val explanation: String
    val defaultValue: Boolean
}