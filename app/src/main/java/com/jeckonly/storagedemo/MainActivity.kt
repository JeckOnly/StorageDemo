package com.jeckonly.storagedemo

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
import com.jeckonly.storagedemo.storageutil.*
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var externalBitmap: Bitmap? by remember {
                mutableStateOf(null)
            }
            var internalBitmap: Bitmap? by remember {
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
                        externalBitmap = tempBitmap
                    }
                }) {
                    Text(text = "从 external storage的应用专属文件夹加载bitmap")
                }
                Button(onClick = {
                   externalBitmap = null
                }) {
                    Text(text = "重置external图片")
                }
                Row {
                    Text(text = "external image:")
                    if (externalBitmap != null) {

                        Image(
                            bitmap = externalBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                Button(onClick = {
                    externalBitmap?.let {
                        scope.launch {
                            val result = saveImageToExternalFilesDir(this@MainActivity, it)
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的image保存到external storage的应用专属文件夹")
                }


                Divider(modifier = Modifier.height(50.dp))


                Button(onClick = {
                    if (deleteImageFromFilesDir(this@MainActivity))
                        Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "删除在internal storage的应用专属文件夹的图片")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormFilesDir(this@MainActivity)
                        internalBitmap = tempBitmap
                    }
                }) {
                    Text(text = "从 internal storage的应用专属文件夹加载bitmap")
                }
                Button(onClick = {
                    internalBitmap = null
                }) {
                    Text(text = "重置internal图片")
                }
                Row {
                    Text(text = "internal image:")
                    if (internalBitmap != null) {

                        Image(
                            bitmap = internalBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                Button(onClick = {
                    externalBitmap?.let {
                        scope.launch {
                            val result = saveImageToFilesDir(this@MainActivity, it)
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的external image保存到 internal storage的应用专属文件夹")
                }
            }
        }
    }
}