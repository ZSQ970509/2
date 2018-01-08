# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5          # 指定代码的压缩级别

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#忽略警告
#-ignorewarning

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepnames class * implements java.io.Serializable

-keep class sun.misc.Unsafe { *; }

-keep class android.support.** {
 *;
}
-dontwarn android.support.**

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}

-keep enum ** { *; }

-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
        public static <fields>;
}

-keepattributes Signature

-keep class com.google.zxing.** {
 *;
}

-keep class com.xsq.common.app.** {
 *;
}

-keep class org.apache.http.** {
 *;
}
-dontwarn org.apache.http.**

-keep class android.net.http.** {
 *;
}
-dontwarn android.net.http.**

-keep class okio.** {
 *;
}
-dontwarn okio.**

-keep class com.squareup.okhttp.** {
 *;
}
-dontwarn com.squareup.okhttp.**

-keep class com.handmark.pulltorefresh.library.** {
 *;
}

-keep class com.j256.ormlite.** {
 *;
}
-dontwarn com.j256.ormlite.**

-keep class com.xsq.common.material.** {
 *;
}
-dontwarn com.xsq.common.material.**