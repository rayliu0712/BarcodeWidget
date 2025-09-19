package com.rayliu0712.barcodewidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text

val counterKey = intPreferencesKey("counterKey")

class MyAppWidget : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      MyContent()
    }
  }

  @Composable
  private fun MyContent() {
    val counter = currentState(counterKey) ?: 0

    Column(
      modifier = GlanceModifier.fillMaxSize(),
      verticalAlignment = Alignment.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "$counter", modifier = GlanceModifier.padding(12.dp))
      Row(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
          text = "Decrease",
          onClick = actionRunCallback<DecreaseAction>()
        )
        Button(
          text = "Increase",
          onClick = actionRunCallback<IncreaseAction>()
        )
      }
    }
  }
}
