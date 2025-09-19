package com.rayliu0712.barcodewidget.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rayliu0712.barcodewidget.MyTheme
import java.io.File

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApp()
    }
  }
}

@Preview(
  showSystemUi = true,
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MyApp() {
  val navController = rememberNavController()
  val barcodeFiles = remember { mutableStateListOf<File>() }

  val context = LocalContext.current
  val dir = File(context.filesDir, "barcodes")
  if (!dir.exists())
    dir.mkdir()
  val files = dir.listFiles().sortedBy { it.nameWithoutExtension }
  barcodeFiles.addAll(files)

  MyTheme {
    NavHost(
      navController,
      startDestination = "home",
    ) {
      composable("home") {
        HomePage(navController, barcodeFiles)
      }
      composable("new_barcode") {
        PageNewBarcode(navController, barcodeFiles)
      }
      composable("edit_barcode/{index}") { backStackEntry ->
        val index = backStackEntry.arguments?.getString("index")!!.toInt()
        PageEditBarcode(navController, barcodeFiles, index)
      }
    }
  }
}
