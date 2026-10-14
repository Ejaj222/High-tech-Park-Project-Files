package com.lurkbd.agent.scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.FileProvider
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DocumentScanHelper {
    fun launchMlKitScanner(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onError: (String) -> Unit = {}
    ) {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(8)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(activity)
            .addOnSuccessListener { intentSender ->
                launcher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "স্ক্যানার চালু করা যায়নি")
            }
    }

    fun parseScanResult(data: Intent?): List<Uri> {
        val result = GmsDocumentScanningResult.fromActivityResultIntent(data) ?: return emptyList()
        val pages = result.pages?.mapNotNull { it.imageUri }.orEmpty()
        if (pages.isNotEmpty()) return pages
        val pdf = result.pdf?.uri
        return if (pdf != null) listOf(pdf) else emptyList()
    }

    fun copyIntoAppStorage(context: Context, source: Uri, preferredName: String? = null): Uri {
        val dir = File(context.filesDir, "scans").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val ext = when {
            source.toString().contains(".pdf", ignoreCase = true) -> "pdf"
            else -> "jpg"
        }
        val dest = File(dir, preferredName ?: "scan_$stamp.$ext")
        context.contentResolver.openInputStream(source)?.use { input ->
            FileOutputStream(dest).use { output -> input.copyTo(output) }
        } ?: error("ফাইল কপি করা যায়নি")
        return Uri.fromFile(dest)
    }

    fun createCameraCaptureUri(context: Context): Uri {
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "capture_$stamp.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
