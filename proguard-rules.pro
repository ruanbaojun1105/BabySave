# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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

#指定代码的压缩级别
    -optimizationpasses 5

    #包明不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses

     #优化  不优化输入的类文件
    -dontoptimize

     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*

    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    -dontwarn android.support.v4.**
    -keep class android.support.v4.**
    -keep interface android.support.v4.app.** {*;}

    -keep class !android.support.v7.view.menu.**,** {*;}

    #忽略警告
    -ignorewarning
    ##记录生成的日志数据,gradle build时在本项目根目录输出##

    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt

    ########记录生成的日志数据，gradle build时 在本项目根目录输出-end######


    #####混淆保护自己项目的部分代码以及引用的第三方jar包library#######
    #-libraryjars libs/android-support-v4.jar
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService

    #如果不想混淆 keep 掉
    -keep class com.lippi.recorder.iirfilterdesigner.** {*; }
    -keep class org.xmlpull.v1.** {*;}
    -keep class rx.internal.util.** { *; }
    #友盟
    -keep class com.umeng.**{*;}
    #项目特殊处理代码

    #忽略警告
    -dontwarn com.lippi.recorder.utils**
    #保留一个完整的包
    -keep class com.lippi.recorder.utils.** {
        *;
     }

    -keep class  com.lippi.recorder.utils.AudioRecorder{*;}


    #如果引用了v4或者v7包
    -dontwarn android.support.**

    ####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }

    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable

    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    #-keepclassmembers enum * {
    #  public static **[] values();
    #  public static ** valueOf(java.lang.String);
    #}

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    #避免混淆泛型 如果混淆报错建议关掉
    #–keepattributes Signature

    #如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
    #gson
    #-libraryjars libs/gson-2.2.2.jar
    #-keepattributes Signature
    # Gson specific classes
    #-keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.examples.android.model.** { *; }

    #UIL
    -keep class com.nostra13.universalimageloader.** { *; }
    -keepclassmembers class com.nostra13.universalimageloader.** {*;}
    #高德地图
    -dontwarn com.amap.api.**
    -dontwarn com.a.a.**
    -dontwarn com.autonavi.**
    -keep class com.amap.api.**  {*;}
    -keep class com.autonavi.**  {*;}
    -keep class com.a.a.**  {*;}


    #IM
    -keepattributes Signature
    -keep class sun.misc.Unsafe { *; }

    -keep class com.taobao.** {*;}
    -keep class com.alibaba.** {*;}
    -keep class com.alipay.** {*;}
    -dontwarn com.taobao.**
    -dontwarn com.alibaba.**
    -dontwarn com.alipay.**

    -keep class com.ut.** {*;}
    -dontwarn com.ut.**

    -keep class com.ta.** {*;}
    -dontwarn com.ta.**
    #特别的 加入自己包中的初始化配置不要混淆
    -keep class me.hibb.mybaby.android.openIM.** {*;}
    -dontwarn me.hibb.mybaby.android.openIM.**
    -keep class mybaby.ui.more.PlaceSettingActivity {*;}

    -keep class anet.**{*;}
    -keep class org.android.spdy.**{*;}
    -keep class org.android.agoo.**{*;}
    -dontwarn anet.**
    -dontwarn org.android.spdy.**
    -dontwarn org.android.agoo.**


    -dontwarn okio.**

    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** { *; }
    -keep interface com.squareup.okhttp.** { *; }

#友盟
    -dontwarn com.ut.mini.**
    -dontwarn okio.**
    -dontwarn com.xiaomi.**
    -dontwarn com.squareup.wire.**
    -dontwarn android.support.v4.**

    -keepattributes *Annotation*

    -keep class android.support.v4.** { *; }
    -keep interface android.support.v4.app.** { *; }

    -keep class okio.** {*;}
    -keep class com.squareup.wire.** {*;}

    -keep class com.umeng.message.protobuffer.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class com.umeng.message.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class org.android.agoo.impl.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class org.android.agoo.service.* {*;}

    -keep class org.android.spdy.**{*;}

    -keep public class **.R$*{
        public static final int *;
    }
    #如果compileSdkVersion为23，请添加以下混淆代码
    -dontwarn org.apache.http.**
    -dontwarn android.webkit.**
    -keep class org.apache.http.** { *; }
    -keep class org.apache.commons.codec.** { *; }
    -keep class org.apache.commons.logging.** { *; }
    -keep class android.net.compatibility.** { *; }
    -keep class android.net.http.** { *; }