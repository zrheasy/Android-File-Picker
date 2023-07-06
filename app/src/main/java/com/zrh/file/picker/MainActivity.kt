package com.zrh.file.picker

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.MediaColumns
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.zrh.file.picker.databinding.ActivityMainBinding
import com.zrh.permission.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSelectFile.setOnClickListener {
            PermissionUtils.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { _, granted ->
                if (granted) selectFile()
            }
        }
    }

    private fun selectFile() {
        FilePicker.pick(this, FilePickOptions(), object : FilePickCallback {
            override fun onResult(data: List<Uri>) {
                getMetaInfo(data)
            }

            override fun onError(code: Int, msg: String) {
                Toast.makeText(this@MainActivity, "$code $msg", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMetaInfo(data: List<Uri>) {
        flow<List<Map<String, Any>>> {

            val res = data.map { uri ->
                val fields = UriUtils.getMetaInfo(this@MainActivity, uri)
                if (!fields.containsKey(MediaColumns.DATA)) {
                    val cache = if (externalCacheDir != null) externalCacheDir else cacheDir
                    val file = UriUtils.getFileFromUri(this@MainActivity, uri, File(cache, "picker"))
                    fields[MediaColumns.DATA] = file.absolutePath
                }
                fields
            }

            emit(res)
        }.flowOn(Dispatchers.IO)
            .onEach { list ->
                val sb = StringBuffer()
                list.forEach { info ->
                    info.entries.forEach { field ->
                        sb.append("\n").append(field.key).append(": ").append(field.value).append("\n")
                    }
                    sb.append("\n----------------------\n")
                }

                mBinding.tvMetaInfo.text = sb.toString()
            }
            .catch {
                Toast.makeText(this@MainActivity, "error: $it", Toast.LENGTH_SHORT).show()
            }
            .launchIn(lifecycleScope)
    }
}