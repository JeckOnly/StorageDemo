package com.jeckonly.storagedemo

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.jeckonly.storagedemo.storageutil.deleteImageFromExternalFilesDir
import com.jeckonly.storagedemo.storageutil.downloadImageToExternalFilesDir
import com.jeckonly.storagedemo.storageutil.loadImageFormExternalFilesDir
import com.jeckonly.storagedemo.storageutil.saveImageToExternalFilesDir
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var bitmap: Bitmap? by remember {
                mutableStateOf(null)
            }
            val scope = rememberCoroutineScope()

            Column {
                Button(onClick = {
                    downloadImageToExternalFilesDir(this@MainActivity)
                }) {
                    Text(text = "从网络下载图片到external storage的应用专属文件夹")
                }
                Button(onClick = {
                    if (deleteImageFromExternalFilesDir(this@MainActivity))
                        Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "删除在external storage的应用专属文件夹的图片")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormExternalFilesDir(this@MainActivity)
                        bitmap = tempBitmap
                    }
                }) {
                    Text(text = "从 external storage的应用专属文件夹加载bitmap")
                }
                Button(onClick = {
                   bitmap = null
                }) {
                    Text(text = "重置图片")
                }
                if (bitmap != null) {

                    Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.size(50.dp))
                }
                Button(onClick = {
                    bitmap?.let {
                        scope.launch {
                            val result = saveImageToExternalFilesDir(this@MainActivity, it)
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的image保存到external storage的应用专属文件夹")
                }
            }
        }
    }
}