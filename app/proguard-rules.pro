# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/oguzemreozcan/Library/Developer/Xamarin/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-libraryjars libs

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-optimizationpasses 5
-verbose
-dontpreverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames
-allowaccessmodification
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

-dontwarn android.support.**
-dontwarn com.makeramen.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.**
-dontwarn com.jeremyfeinstein.**
-dontwarn com.makeramen.**
-dontwarn android.support.v7.**
-dontwarn org.joda.**
-dontwarn com.squareup.okhttp.**
-dontwarn de.hdodenhof.circleimageview
-dontwarn org.apache.http.**
-dontwarn com.google.android.gms.**
-dontwarn com.github.citux.datetimepicker.**
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

#-keep public class com.google.** {*;}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
#-keep public class * extends com.armut.armutha.BasicFragment
#-keep public class com.armut.armutha.BasicFragment
#-keep public class com.armut.armutha.BasicStaticQuestionFragment
-keep interface com.armut.armutha.**{*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,Interfaces
-keep class com.android.internal.os{*;}

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v8.renderscript.** {*;}
-keep interface android.support.v8.renderscript.** {*;}

-keep class com.makeramen.** {*;}
-keep interface com.makeramen.** { *; }
-keep class de.hdodenhof.**{*;}
-keep class com.github.citux.**{*;}
-keep class java.util.**{*;}
#-keep class com.makeramen.roundedimageview.RoundedImageView
-keep interface com.jeremyfeinstein.** {*;}
-keep class com.jeremyfeinstein.** {*;}
#-keep class com.crashlytics.** { *; }
-keep interface com.nostra13.**{*;}
-keep class com.nostra13.**{*;}
-keep interface com.squareup.okhttp.**{*;}
-keep class com.squareup.okhttp.**{*;}
-keep class eu.janmuller.android.simplecropimage.**{*;}
-keep interface eu.janmuller.android.simplecropimage.**{*;}
#-keep class com.birin.gridlistviewadapters.**{*;}
#-keep interface com.birin.gridlistviewadapters.**{*;}
-keep class android.app.**{*;}
-keep class android.transition.**{*;}
#-keep class android.location.**{*;}
#-keep interface android.location.**{*;}

-keep class * extends java.lang.Throwable
-keep class android.support.v7.widget.** { *; }
-keep class * implements java.io.Serializable { *; }
-keep class com.facebook.** { *; }
-keep class org.joda.** { *; }
-keep interface org.joda.** { *; }
#-keep class android.support.** { *; }
#-keep interface android.support.** { *; }

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * {
    public protected *;
}

-keepclassmembers class * implements java.io.Serializable{
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------

-keep class * extends java.util.ListResourceBundle {
      protected Object[][] getContents();
  }

  -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
      public static final *** NULL;
  }

  -keepnames @com.google.android.gms.common.annotation.KeepName class *
  -keepclassmembernames class * {
      @com.google.android.gms.common.annotation.KeepName *;
  }

  -keepnames class * implements android.os.Parcelable {
      public static final ** CREATOR;
  }

  -dontwarn com.squareup.okhttp.**
  -dontwarn com.google.appengine.api.urlfetch.**
  -dontwarn com.twitter.sdk.**
  -dontwarn rx.**
  -dontwarn retrofit.**
  -dontwarn com.mixpanel.**
  -keep class retrofit.** { *; }
  -keepclasseswithmembers class * {
      @retrofit.http.* <methods>;
  }

  # OrmLite uses reflection
  -keep class com.j256.**
  -keep class com.j256.** {
     *;
  }
  -keepclassmembers class com.j256.** { *; }
  -keep enum com.j256.**
  -keepclassmembers enum com.j256.** { *; }
  -keep interface com.j256.**
  -keepclassmembers interface com.j256.** { *; }

  # New Relic
  -keep class com.newrelic.** { *; }
  -dontwarn com.newrelic.**
  -keepattributes Exceptions, Signature, InnerClasses

  -keepclassmembers class * {
    public <init>(android.content.Context);
  }

  #Otto
  -keepclassmembers class ** {
      @com.squareup.otto.Subscribe public *;
      @com.squareup.otto.Produce public *;
  }

  #Google Maps
  -keep class com.google.android.gms.maps.** { *; }
  -keep interface com.google.android.gms.maps.** { *; }

  ##ADJUST
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }
    -keep class com.adjust.sdk.plugin.MacAddressUtil {
        java.lang.String getMacAddress(android.content.Context);
    }
    -keep class com.adjust.sdk.plugin.AndroidIdUtil {
        java.lang.String getAndroidId(android.content.Context);
    }
    -keep class com.google.android.gms.common.ConnectionResult {
        int SUCCESS;
    }
    -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
        com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
    }
    -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
        java.lang.String getId();
        boolean isLimitAdTrackingEnabled();
    }
    -keep class dalvik.system.VMRuntime {
        java.lang.String getRuntime();
    }
    -keep class android.os.Build {
        java.lang.String[] SUPPORTED_ABIS;
        java.lang.String CPU_ABI;
    }
    -keep class android.content.res.Configuration {
        android.os.LocaledList getLocales();
        java.util.Locale locale;
    }
    -keep class android.os.LocaledList {
        java.util.Locale get(int);
    }

    -keep class com.firebase.** { *; }
    -keep class org.apache.** { *; }
    -keepnames class com.fasterxml.jackson.** { *; }
    -keepnames class javax.servlet.** { *; }
    -keepnames class org.ietf.jgss.** { *; }
    -dontwarn org.w3c.dom.**
    -dontwarn org.joda.time.**
    -dontwarn org.shaded.apache.**
    -dontwarn org.ietf.jgss.**