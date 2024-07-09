package com.orbits.paymentmoduletwo.helper

import android.os.Build
import com.orbits.paymentmoduletwo.BuildConfig


object Constants {
    const val KUWAIT_FLAG: String = "https://admin-dev.fashionzat.com/uploads/164336808161f3ce919b8717.89263080.png"

    const val PREFS_DEEPLINK_TARGET = "deeplink_target"
    const val PREFS_isFROM_DEEPLINK = "is_from_deeplink"
    const val PREFS_DEEPLINK_ID = "deeplink_id"
    const val PREFS_DEEPLINK_NAME = "deeplink_name"
    const val PREFS_DEEPLINK_SECONDARY_ID = "deeplink_pharmacy_id"

    var DEVICE_TOKEN = ""
    val DEVICE_MODEL: String = Build.MODEL
    const val DEVICE_TYPE = "A" //passed in banners
    val OS_VERSION = Build.VERSION.RELEASE
    const val APP_VERSION = BuildConfig.VERSION_NAME


    //Date Format
    const val DATE_DAY_FORMAT = "yyyy-MM-dd EEE"
    const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"
    const val BASIC_DATE_FORMAT = "dd-MM-yyyy"
    const val BASIC_DATE_FORMAT_WITH_SLASH = "dd/MM/yyyy"
    const val SIMPLE_TIME_HOUR_FORMAT = "HH:mm"
    var SIMPLE_TIME_FORMAT = "HH:mm:ss"

    var SimpleTimeFormat = "HH:mm:ss"
    var SimpleDateFormat = "yyyy-MM-dd"
    var SimpleDateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    var ZONE_UTC = "utc"
    var SimpleTimeHourFormat = "hh:mm aa"

    var DisplayDateFormat = "dd MMM, yyyy"

    const val COMMON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_PICKER_DATE_FORMAT = "yyyy-MM-dd"
    const val FORMATTED_DATE = "dd MMMM yyyy"
    const val DATE_FORMAT_WITH_DAY = "EEEE, MMMM dd"


    var DEVICE_DENSITY = 0.0
    var ZONE_KUWAIT = "kuwait"
    var NO_ZONE = "no_zone"

    val fontBold = "bold"
    val fontRegular = "regular"
    val fontMedium = "medium"
    val fontLight = "light"
    val fontRegularRev = "regular_reverse"


    const val TOOLBAR_ICON_ONE = "iconOne"
    const val TOOLBAR_ICON_TWO = "iconTwo"

    const val PB_target = "target"
    const val PB_target_id = "target_id"
    const val PB_secondary_id = "secondary_id"
    const val PB_title_en = "title_en"
    const val PB_title_ar = "title_ar"

}

object DeepLinkTargets {
   /** define all targets here add use this from object for both pushwoosh & branch*/
    const val exampleListing = "abc"
    const val exampleDetails = "xyz"
    const val exampleExternalWeb = "e"
    /** remove or modify this example variables this is only for reference*/
}