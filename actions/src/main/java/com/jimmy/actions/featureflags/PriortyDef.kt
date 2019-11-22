package com.jimmy.actions.featureflags

import android.support.annotation.IntDef


// Define the list of accepted constants and declare the NavigationMode annotation
@Retention(AnnotationRetention.SOURCE)
@IntDef(LOW_PRIORITY, MEDIUM_PRIORTY, HIGH_PRIORTY, TEST_PRIORTY)
annotation class Priorety

// Declare the constants
const val LOW_PRIORITY = 0
const val MEDIUM_PRIORTY = 1
const val HIGH_PRIORTY = 2
const val TEST_PRIORTY = 3

/*
abstract class PriortyDef {

    // Decorate the target methods with the annotation
    // Attach the annotation
    @get:Priorety
    @setparam:Priorety
    abstract var priorety: Int

}*/
