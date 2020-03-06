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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontobfuscate
-dontwarn com.squareup.picasso.OkHttpDownloader
-dontwarn com.google.api.client.googleapis.testing.TestUtils
-dontwarn java.nio.file.*
-dontwarn java.lang.invoke.*
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn java.lang.reflect.Method
-dontwarn com.comscore.instrumentation.InstrumentedMapActivity
-dontwarn org.apache.http.entity.mime.content.FileBody
-dontwarn org.apache.http.entity.mime.AbstractMultipartForm
-dontwarn org.apache.http.entity.mime.FormBodyPart
-dontwarn org.apache.http.entity.mime.FormBodyPartBuilder
-dontwarn org.apache.http.entity.mime.MIME
-dontwarn org.apache.http.entity.mime.MultipartEntityBuilder
-dontwarn org.apache.http.entity.mime.MultipartFormEntity
-dontwarn org.apache.http.entity.mime.content.AbstractContentBody
-dontwarn org.apache.http.entity.mime.content.ByteArrayBody
-dontwarn org.apache.http.entity.mime.content.InputStreamBody
-dontwarn org.apache.http.entity.mime.content.StringBody
-dontwarn org.apache.**
-dontwarn android.view.autofill.AutofillManager
-dontwarn javax.imageio.ImageIO
-dontwarn java.awt.image.BufferedImage
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
-keep class * implements com.coremedia.iso.boxes.Box { *; }
-dontwarn com.coremedia.iso.boxes.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.mjpeg.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.ttml.**
-dontwarn com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
-dontwarn com.android.installreferrer.api.*
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn kotlinx.coroutines.flow.**
-dontwarn org.reactivestreams.FlowAdapters
-dontwarn org.reactivestreams.**
-dontwarn java.util.concurrent.flow.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keep class org.json.** { *; }