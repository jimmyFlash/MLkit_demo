const val kotlinVersion = "1.3.41"
const val googlePlayServicesVersion  = "4.2.0"

object BuildPlugins {

    private object Versions {
        const val buildToolsVersion = "3.4.2"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
}

object GoogleServices{
    const val googlePlayServices = "com.google.gms:google-services:$googlePlayServicesVersion"
}

object AndroidSdk {
    const val min = 23
    const val compile = 28
    const val target = compile
}

object Libraries {

    private object Versions {
        const val jetpack = "1.0.0-beta01"
        const val constraintLayout = "1.1.3"
        const val ktx = "1.1.0-alpha05"
        const val support = "28.0.0"
        const val archLifecycle = "1.0.0"
        const val archLifecycleCommon = "1.1.1"
        const val workManagerKotlin = "1.0.0-alpha10"
        const val motionlayoutSupport = "1.0.0-alpha10"

        const val paracamera = "0.2.2"
    }

    const val kotlinStdLib        = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    const val appCompat7          = "com.android.support:appcompat-v7:${Versions.support}"
    const val appCompat4          = "com.android.support:appcompat-v4:${Versions.support}"
    const val mediaCompat         = "com.android.support:support-media-compat:${Versions.support}"
    const val designCompat        = "com.android.support:design:${Versions.support}"
    const val exifSupport         = "com.android.support:exifinterface:${Versions.support}"
    const val constraintLayout    = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"
    const val archLifecycle       = "android.arch.lifecycle:extensions:${Versions.archLifecycle}"
    const val archLifecycleComm   = "android.arch.lifecycle:common-java8:${Versions.archLifecycleCommon}"
    const val workManagerExt      = "android.arch.work:work-runtime-ktx:${Versions.workManagerKotlin}"
    const val montionLayout       = "com.android.support.constraint:constraint-layout:${Versions.motionlayoutSupport}"


    const val minorksParacamera = "com.mindorks:paracamera:${Versions.paracamera}"
    //const val ktxCore          = "androidx.core:core-ktx:${Versions.ktx}"
}

object TestLibraries {
    private object Versions {
        const val junit4 = "4.12"
        const val testRunner = "1.0.2"
        const val espresso = "3.0.2"
    }
    const val junit4     = "junit:junit:${Versions.junit4}"
    const val testRunner = "com.android.support.test:runner:${Versions.testRunner}"
    const val espresso   = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
}

object FireBase {

    private object FBCore {
        const val core = "16.0.4"
    }

    private object MLKit {
        const val vision = "18.0.1"
        const val imageLable = "17.0.2"
    }

    const val fireBaseCore = "com.google.firebase:firebase-core:${FBCore.core}"
    const val mlKitVisionAPI = "com.google.firebase:firebase-ml-vision:${MLKit.vision}"
    const val mlKitVisionImageLabel = "com.google.firebase:firebase-ml-vision:${MLKit.imageLable}"
}