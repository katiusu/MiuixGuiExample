# ScreenRecorderControlClient binds this service by the fixed class-name string in
# ScreenRecorderContract. Keep the complete IPC contract stable so the optimized
# manifest and the target recorder process always agree on the component name.
-keep class com.katiusu.miuixexample.screenrecorder.ScreenRecorderControlService { *; }
-keep class com.katiusu.miuixexample.screenrecorder.ScreenRecorderContract { *; }
-keep class com.katiusu.miuixexample.screenrecorder.RecorderSnapshot { *; }

# Preserve metadata used by Kotlin/Compose.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,Signature,InnerClasses,EnclosingMethod
