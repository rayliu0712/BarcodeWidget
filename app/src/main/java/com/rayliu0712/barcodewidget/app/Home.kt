package com.rayliu0712.barcodewidget.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import java.io.File

@Composable
fun HomePage(
  navController: NavController,
  barcodes: SnapshotStateList<File>
) {
  Scaffold(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    floatingActionButton = {
      ExtendedFloatingActionButton(
        text = { Text("新增條碼") },
        icon = {
          Icon(
            Icons.Default.Add,
            "新增條碼",
          )
        },
        onClick = { navController.navigate("new_barcode") },
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      items(barcodes.size) { i ->
        ListItem(
          modifier = Modifier.clickable(
            onClick = {
              navController.navigate("edit_barcode/$i")
            }
          ),
          colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
          ),
          headlineContent = { Text(barcodes[i].nameWithoutExtension) },
        )
        HorizontalDivider()
      }
    }
  }
}
