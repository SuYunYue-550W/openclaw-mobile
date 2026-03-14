package com.openclaw.mobile.security

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 权限管理器
 * 
 * 功能：
 * 1. 动态申请权限
 * 2. 权限状态检查
 * 3. 权限使用记录
 * 4. 引导用户开启必要权限
 */
@Singleton
class PermissionManager @Inject constructor() {

    sealed class PermissionStatus {
        object Granted : PermissionStatus()
        object Denied : PermissionStatus()
        object DeniedPermanently : PermissionStatus()
        object NotRequested : PermissionStatus()
    }

    data class PermissionRequest(
        val permission: String,
        val purpose: String,
        val required: Boolean = false
    )

    private val _permissionState = MutableStateFlow<Map<String, PermissionStatus>>(emptyMap())
    val permissionState: StateFlow<Map<String, PermissionStatus>> = _permissionState.asStateFlow()

    /**
     * 权限组定义
     */
    object PermissionGroups {
        // 网络权限
        val NETWORK = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        // 存储权限 (Android 12 及以下)
        val STORAGE_LEGACY = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // Android 13+ 媒体权限
        val MEDIA = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            STORAGE_LEGACY
        }

        // 通知权限 (Android 13+)
        val NOTIFICATION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }

        // 录音权限
        val AUDIO = arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    /**
     * 检查权限状态
     */
    fun checkPermission(context: Context, permission: String): PermissionStatus {
        return when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                PermissionStatus.Granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission
            ) -> {
                PermissionStatus.Denied
            }
            else -> {
                PermissionStatus.DeniedPermanently
            }
        }
    }

    /**
     * 批量检查权限
     */
    fun checkPermissions(context: Context, permissions: Array<String>): Map<String, PermissionStatus> {
        return permissions.associateWith { checkPermission(context, it) }
    }

    /**
     * 请求权限
     */
    fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest, requestCode)
        }
    }

    /**
     * 处理权限请求结果
     */
    fun handlePermissionResult(
        permissions: Array<String>,
        grantResults: IntArray
    ): Map<String, PermissionStatus> {
        val result = mutableMapOf<String, PermissionStatus>()
        permissions.forEachIndexed { index, permission ->
            val status = if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                PermissionStatus.Granted
            } else {
                PermissionStatus.Denied
            }
            result[permission] = status
        }
        _permissionState.value = result
        return result
    }

    /**
     * 检查是否所有权限都已授予
     */
    fun allPermissionsGranted(results: Map<String, PermissionStatus>): Boolean {
        return results.values.all { it == PermissionStatus.Granted }
    }

    /**
     * 打开应用设置页面
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    /**
     * 请求忽略电池优化（用于后台模型推理）
     */
    fun requestIgnoreBatteryOptimization(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoring = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
            
            if (!isIgnoring) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(intent)
            }
        }
    }

    /**
     * 检查存储访问权限（Scoped Storage）
     */
    fun hasStorageAccess(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 请求所有文件访问权限（Android 11+）
     */
    fun requestAllFilesAccess(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            activity.startActivity(intent)
        }
    }
}
