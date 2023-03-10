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
                        // Android 10 ???????????????????????????????????????dialog????????????????????????????????????
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
                    Text(text = "external storage??????????????????")
                }
                Button(onClick = {
                    downloadImageToExternalFilesDir(this@MainActivity, networkImageName)
                }) {
                    Text(text = "????????????????????????external storage????????????????????????")
                }
                Button(onClick = {
                    scope.launch {
                        saveImageToExternalFilesDir(this@MainActivity, BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.boy), resourceImageName)
                    }
                }) {
                    Text(text = "???resources????????????????????????external storage????????????????????????")
                }
                Button(onClick = {
                    if (deleteImageFromExternalFilesDir(this@MainActivity, resourceImageName))
                        Toast.makeText(this@MainActivity, "????????????", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "?????????external storage?????????????????????????????????")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormExternalFilesDir(this@MainActivity, resourceImageName)
                        externalBitmap = tempBitmap
                    }
                }) {
                    Text(text = "??? external storage??????????????????????????????bitmap")
                }
                Button(onClick = {
                    externalBitmap = null
                }) {
                    Text(text = "??????external??????")
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
                                Toast.makeText(this@MainActivity, "????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "????????????image?????????external storage????????????????????????")
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
                        Toast.makeText(this@MainActivity, "????????????", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "?????????internal storage?????????????????????????????????")
                }
                Button(onClick = {
                    scope.launch {
                        val tempBitmap = loadImageFormFilesDir(this@MainActivity, resourceImageName)
                        internalBitmap = tempBitmap
                    }
                }) {
                    Text(text = "??? internal storage??????????????????????????????bitmap")
                }
                Button(onClick = {
                    internalBitmap = null
                }) {
                    Text(text = "??????internal??????")
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
                                Toast.makeText(this@MainActivity, "????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "????????????external image????????? internal storage????????????????????????")
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
                    Text(text = "???????????? external public ?????????")
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
                                Toast.makeText(this@MainActivity, "????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text(text = "????????????external image????????? external public ???")
                }

                Button(onClick = {
                    downloadImageToExternalPublic(this@MainActivity, networkImageName)
                }) {
                    Text(text = "????????????????????????external storage public")
                }

                Button(onClick = {
                    scope.launch {
                        externalPublicImages =
                            loadImageFromExternalPublicStorage(this@MainActivity.contentResolver)
                    }
                }) {
                    Text(text = "?????? external public ??????image???????????????????????????????????????????????????")
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
                                        // ??????
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
                    Text(text = "??????????????????????????? external public , ??????????????????????????????????????????")
                }
            }
        }
    }
}