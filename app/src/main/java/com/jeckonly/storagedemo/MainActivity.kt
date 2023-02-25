package com.jeckonly.storagedemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.jeckonly.storagedemo.storageutil.*
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : FragmentActivity() {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var deletedImageUri: Uri? = null

    val networkImageName = "networkImage.jpg"

    val resourceImageName = "resourceImage.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        // Android 10 删除其他应用的文件第一次按dialog，允许删除仅仅只是授权。
                        lifecycleScope.launch {
                            deletedImageUri?.let { it1 ->
                                deleteImageFromExternalPublicStorage(
                                    this@MainActivity.contentResolver, intentSenderLauncher,
                                    it1
                                )
                            } ?: return@launch
                        }
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Photo deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Photo couldn't be deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        setContent {

            var externalBitmap: Bitmap? by remember {
                mutableStateOf(null)
            }
            var internalBitmap: Bitmap? by remember {
                mutableStateOf(null)
            }
            var externalPublicImages: List<SharedStoragePhoto> by remember {
                mutableStateOf(emptyList())
            }
            val scope = rememberCoroutineScope()

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), contentAlignment = Alignment.Center
                ) {
                    Text(text = "external storage中的应用专属")
                }
                Button(onClick = {
                    downloadImageToExternalFilesDir(this@MainActivity, networkImageName)
                }) {
                    Text(text = "从网络下载图片到external storage的应用专属文件夹")
                }
                Button(onClick = {
                    scope.launch {
                        saveImageToExternalFilesDir(this@MainActivity, BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.boy), resourceImageName)
                    }
                }) {
                    Text(text = "从resources文件夹下载图片到external storage的应用专属文件夹")
                }
                Button(onClick = {
                    if (deleteImageFromExternalFilesDir(this@MainActivity, resourceImageName))
                        Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "删除在external storage的应用专属文件夹的图片")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormExternalFilesDir(this@MainActivity, resourceImageName)
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
                            val result = saveImageToExternalFilesDir(this@MainActivity, it, UUID.randomUUID().toString() + ".jpg")
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的image保存到external storage的应用专属文件夹")
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), contentAlignment = Alignment.Center
                ) {
                    Text(text = "internal storage")
                }
                Button(onClick = {
                    if (deleteImageFromFilesDir(this@MainActivity, resourceImageName))
                        Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "删除在internal storage的应用专属文件夹的图片")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormFilesDir(this@MainActivity, resourceImageName)
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
                            val result = saveImageToFilesDir(this@MainActivity, it, resourceImageName)
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的external image保存到 internal storage的应用专属文件夹")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), contentAlignment = Alignment.Center
                ) {
                    Text(text = "external storage public")
                }

                Button(onClick = {
                    askPermissionForExternalPublicStorage(this@MainActivity)
                }) {
                    Text(text = "请求关于 external public 的权限")
                }

                Button(onClick = {
                    externalBitmap?.let {
                        scope.launch {
                            val result = saveImageToExternalPublicStorage(
                                this@MainActivity.contentResolver,
                                resourceImageName,
                                it
                            )
                            if (result)
                                Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "把展示的external image保存到 external public 中")
                }

                Button(onClick = {
                    downloadImageToExternalPublic(this@MainActivity, networkImageName)
                }) {
                    Text(text = "从网络下载图片到external storage public")
                }

                Button(onClick = {
                    scope.launch {
                        externalPublicImages =
                            loadImageFromExternalPublicStorage(this@MainActivity.contentResolver)
                    }
                }) {
                    Text(text = "搜索 external public 中的image并展示（有权限才能看到其他应用的）")
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    for (image in externalPublicImages) {
                        Column {
                            Text(text = image.name)
                            AsyncImage(
                                model = image.contentUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable {
                                        // 删除
                                        scope.launch {
                                            deleteImageFromExternalPublicStorage(
                                                this@MainActivity.contentResolver,
                                                intentSenderLauncher,
                                                image.contentUri
                                            )
                                            deletedImageUri = image.contentUri
                                        }
                                    }
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .width(5.dp)
                                .background(Color.Black)
                        )
                    }
                }

                Button(onClick = {
                    // do nothing
                }) {
                    Text(text = "点击上面的图片删除 external public , 其他应用的可删；需要手动刷新")
                }
            }
        }
    }
}