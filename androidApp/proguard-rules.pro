# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-verbose
-allowaccessmodification

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-keepattributes LocalVariableTable, LocalVariableTypeTable
# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-optimizations !method/removal/parameter
# Repackage classes into the top-level.
-repackageclasses
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep,allowobfuscation,allowshrinking class * extends androidx.navigation.Navigator
-keep,allowobfuscation,allowshrinking @interface *

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class tech.alexi.yaba.**$$serializer { *; }
-keepclassmembers class tech.alexi.yaba.** {
    *** Companion;
}
-keepclasseswithmembers class tech.alexi.yaba.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class * extends kotlinx.serialization.KSerializer { *; }

-keep class kotlinx.datetime.** { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class * extends okhttp3.Interceptor { *; }
