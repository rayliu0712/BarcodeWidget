package com.rayliu0712.barcodewidget.app

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rayliu0712.barcodewidget.R
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageNewBarcode(
  navController: NavController,
  barcodeFiles: SnapshotStateList<File>,
) {
  val context = LocalContext.current

  val barcodeName = remember { mutableStateOf("") }
  val barcodeBitmap = remember { mutableStateOf<Bitmap?>(null) }
  var needCheck by remember { mutableStateOf(true) }

  val isNameUsed = remember {
    derivedStateOf {
      needCheck && barcodeFiles.any {
        it.nameWithoutExtension == barcodeName.value
      }
    }
  }
  val canSave by remember {
    derivedStateOf {
      barcodeName.value.isNotBlank() &&
              !isNameUsed.value &&
              barcodeBitmap.value != null
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    topBar = {
      TopAppBar(
        colors = myTopAppBarColors(),
        title = { Text("新增條碼") },
        navigationIcon = { CloseButton(navController) },
        actions = {
          Button(
            enabled = canSave,
            onClick = {
              // 儲存新增的條碼

              needCheck = false

              val file = syncCreate(
                context,
                barcodeName.value,
                barcodeBitmap.value!!
              )
              barcodeFiles.add(file)
              barcodeFiles.sortBy { it.nameWithoutExtension }

              navController.popBackStack()
            }
          ) { Text("儲存") }
        }
      )
    }
  ) { innerPadding ->
    Body(barcodeName, barcodeBitmap, isNameUsed, innerPadding)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageEditBarcode(
  navController: NavController,
  barcodeFiles: SnapshotStateList<File>,
  barcodeIndex: Int
) {
  val context = LocalContext.current
  val oldFile = remember { barcodeFiles[barcodeIndex] }

  val barcodeName = remember {
    mutableStateOf(oldFile.nameWithoutExtension)
  }
  val barcodeBitmap = remember {
    val bitmap = ImageDecoder.decodeBitmap(
      ImageDecoder.createSource(oldFile)
    )
    mutableStateOf<Bitmap?>(bitmap)
  }
  var expanded by remember { mutableStateOf(false) }
  var needCheck by remember { mutableStateOf(true) }

  val isNameUsed = remember {
    derivedStateOf {
      needCheck && barcodeFiles
        .withIndex()
        .any { (i, file) ->
          i != barcodeIndex && file.nameWithoutExtension == barcodeName.value
        }
    }
  }
  val canSave by remember {
    derivedStateOf {
      barcodeName.value.isNotBlank() &&
              oldFile.nameWithoutExtension != barcodeName.value &&
              !isNameUsed.value &&
              barcodeBitmap.value != null
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    topBar = {
      TopAppBar(
        colors = myTopAppBarColors(),
        title = { Text("編輯條碼") },
        navigationIcon = { CloseButton(navController) },
        actions = {
          Button(
            enabled = canSave,
            onClick = {
              // 儲存編輯後的條碼

              needCheck = false

              val newFile = syncEdit(
                context,
                oldFile,
                barcodeName.value,
                barcodeBitmap.value!!
              )
              barcodeFiles.removeAt(barcodeIndex)
              barcodeFiles.add(newFile)
              barcodeFiles.sortBy { it.nameWithoutExtension }

              navController.popBackStack()
            }
          ) { Text("儲存") }

          Box {
            IconButton(
              onClick = { expanded = !expanded }
            ) {
              Icon(
                Icons.Default.MoreVert,
                "更多"
              )
            }

            DropdownMenu(
              expanded = expanded,
              onDismissRequest = { expanded = false },
              containerColor = MaterialTheme.colorScheme.surface,
            ) {
              DropdownMenuItem(
                text = { Text("刪除條碼") },
                leadingIcon = {
                  Icon(
                    painterResource(R.drawable.delete),
                    "刪除條碼"
                  )
                },
                onClick = {
                  // 刪除條碼

                  syncDelete(barcodeFiles[barcodeIndex])
                  barcodeFiles.removeAt(barcodeIndex)

                  expanded = false
                  navController.popBackStack()
                }
              )
            }
          }
        }
      )
    }
  ) { innerPadding ->
    Body(barcodeName, barcodeBitmap, isNameUsed, innerPadding)
  }
}

@Composable
private fun CloseButton(navController: NavController) {
  IconButton(
    onClick = { navController.popBackStack() },
  ) {
    Icon(Icons.Default.Close, "關閉")
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun myTopAppBarColors(): TopAppBarColors {
  return TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer
  )
}


@Composable
private fun Body(
  barcodeName: MutableState<String>,
  barcodeBitmap: MutableState<Bitmap?>,
  isNameUsed: State<Boolean>,
  innerPadding: PaddingValues,
) {
  val context = LocalContext.current
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri == null)
      return@rememberLauncherForActivityResult

    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source)
    val result = decodeBarcode(bitmap)

    if (result == null)
      Log.d("tag", "zxing failed")
    else
      barcodeBitmap.value = encodeBarcode(
        result.text,
        result.barcodeFormat,
      )
  }

  Column(
    modifier = Modifier.padding(innerPadding)
  ) {
    OutlinedTextField(
      value = barcodeName.value,
      onValueChange = { barcodeName.value = it },
      label = { Text("名稱") },
      supportingText = {
        if (isNameUsed.value)
          Text("此名稱已被使用")
      },
      isError = isNameUsed.value
    )

    if (barcodeBitmap.value == null)
      Image(painterResource(R.drawable.image), null)
    else
      Image(barcodeBitmap.value!!.asImageBitmap(), null)

    Button(
      onClick = { launcher.launch("image/*") },
      contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
      modifier = Modifier
        .height(56.dp)
    ) {
      Icon(
        painterResource(R.drawable.image),
        contentDescription = "選擇圖片並掃描條碼",
        modifier = Modifier.size(ButtonDefaults.IconSize)
      )
      Spacer(Modifier.size(ButtonDefaults.IconSpacing))
      Text("選擇圖片")
    }
    Spacer(Modifier.size(16.dp))
    FilledTonalButton(
      onClick = { /* launch scanner */ },
      contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
      modifier = Modifier
        .height(56.dp)
    ) {
      Icon(
        painterResource(R.drawable.qr_code_scanner),
        contentDescription = "掃描條碼",
        modifier = Modifier.size(ButtonDefaults.IconSize)
      )
      Spacer(Modifier.size(ButtonDefaults.IconSpacing))
      Text("掃描條碼")
    }
  }
}
