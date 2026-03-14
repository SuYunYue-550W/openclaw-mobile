# OpenClaw Mobile ProGuard Rules

# 保留 Kotlin 协程
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# 保留 Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# 保留 Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# 保留 Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 保留数据模型
-keep class com.openclaw.mobile.api.** { *; }
-keep class com.openclaw.mobile.inference.** { *; }

# 保留 ONNX Runtime (如果使用)
-keep class com.microsoft.onnxruntime.** { *; }

# 保留 Tink 加密库
-keep class com.google.crypto.tink.** { *; }

# 保留 MLC LLM (如果使用)
# -keep class org.mlc.** { *; }

# 不混淆类名（调试用，发布时可移除）
-dontobfuscate

# 打印混淆映射
-printmapping mapping.txt
