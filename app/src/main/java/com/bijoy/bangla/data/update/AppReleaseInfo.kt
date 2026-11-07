package com.bijoy.bangla.data.update

import java.io.File

data class AppReleaseInfo(
    val tagName: String,
    val name: String,
    val body: String,
    val apkUrl: String,
    val apkName: String,
    val apkSize: Long,
    val publishedAt: String,
    val htmlUrl: String
)

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(val currentVersion: String, val release: AppReleaseInfo) : UpdateStatus()
    data class UpToDate(val currentVersion: String, val release: AppReleaseInfo?) : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progressPercent: Int, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
    data class Completed(val apkFile: File, val release: AppReleaseInfo) : DownloadState()
    data class Failed(val error: String) : DownloadState()
}
