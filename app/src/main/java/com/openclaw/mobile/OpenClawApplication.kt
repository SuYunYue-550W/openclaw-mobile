package com.openclaw.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * OpenClaw Mobile Application
 * 
 * 应用入口类，初始化全局组件
 */
@HiltAndroidApp
class OpenClawApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // 初始化安全组件
        // SecurityInitializer.init(this)
        
        // 初始化日志系统
        // Logger.init()
    }
}
