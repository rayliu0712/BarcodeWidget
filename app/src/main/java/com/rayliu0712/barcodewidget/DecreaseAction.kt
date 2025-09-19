package com.rayliu0712.barcodewidget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState

class DecreaseAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    Log.d("tag", "decreaseAction")
    updateAppWidgetState(context, glanceId) { prefs ->
      prefs[counterKey] = (prefs[counterKey] ?: 0) - 1
    }
    MyAppWidget().update(context, glanceId)
  }
}