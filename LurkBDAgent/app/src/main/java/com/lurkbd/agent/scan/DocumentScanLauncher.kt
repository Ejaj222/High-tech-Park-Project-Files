package com.lurkbd.agent.scan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

/**
 * Returns a launcher lambda that prefers ML Kit Document Scanner,
 * falling back to CameraX [CameraCaptureActivity] when Play Services scan fails.
 */
@Composable
fun rememberDocumentScanLauncher(
    subject: String,
    onSaved: (uri: String, mime: String, title: String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val activity = context as Activity

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val data = result.data
        val path = data?.getStringExtra(CameraCaptureActivity.EXTRA_PATH)
        val uri = data?.data?.toString() ?: path?.let { Uri.fromFile(java.io.File(it)).toString() }
        if (uri != null) {
            onSaved(uri, "image/jpeg", "স্ক্যান · $subject")
            Toast.makeText(context, "ক্যামেরা স্ক্যান সেভ হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    val mlKitLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val uris = DocumentScanHelper.parseScanResult(result.data)
        if (uris.isEmpty()) {
            Toast.makeText(context, "কোনো পেজ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        uris.forEachIndexed { index, uri ->
            runCatching {
                val stored = DocumentScanHelper.copyIntoAppStorage(context, uri)
                val mime = if (stored.toString().endsWith(".pdf")) "application/pdf" else "image/jpeg"
                onSaved(stored.toString(), mime, "স্ক্যান ${index + 1} · $subject")
            }.onFailure {
                Toast.makeText(context, "সেভ ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(context, "${uris.size}টি ডকুমেন্ট সেভ হয়েছে", Toast.LENGTH_SHORT).show()
    }

    return remember(subject) {
        {
            DocumentScanHelper.launchMlKitScanner(
                activity = activity,
                launcher = mlKitLauncher,
                onError = {
                    // Fallback to CameraX capture activity
                    Toast.makeText(context, "ML Kit অনুপলব্ধ — CameraX চালু হচ্ছে", Toast.LENGTH_SHORT).show()
                    cameraLauncher.launch(Intent(context, CameraCaptureActivity::class.java))
                }
            )
            // Also request camera permission preemptively for fallback
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA), 42)
        }
    }
}
