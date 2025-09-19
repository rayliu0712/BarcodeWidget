package com.rayliu0712.barcodewidget.app

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.io.File
import java.util.EnumMap
import kotlin.collections.set

fun decodeBarcode(bitmap: Bitmap): Result? {
  val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
  val width = mutableBitmap.width
  val height = mutableBitmap.height
  val pixels = IntArray(width * height)
  mutableBitmap.getPixels(
    pixels,
    0,
    width,
    0,
    0,
    width,
    height
  )

  val source = RGBLuminanceSource(width, height, pixels)
  val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
  val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
  hints[DecodeHintType.TRY_HARDER] = true

  return try {
    MultiFormatReader().decode(binaryBitmap, hints)
  } catch (_: NotFoundException) {
    null
  }
}

fun encodeBarcode(
  content: String,
  barcodeFormat: BarcodeFormat,
): Bitmap {
  val width = 400
  val height = 100

  val bitMatrix = MultiFormatWriter().encode(
    content,
    barcodeFormat,
    width,
    height
  )

  val pixels = IntArray(width * height)
  for (y in 0 until height) {
    for (x in 0 until width) {
      pixels[y * width + x] =
        if (bitMatrix.get(x, y)) 0xFF000000.toInt()
        else 0xFFFFFFFF.toInt()
    }
  }

  val bitmap = createBitmap(width, height)
  bitmap.setPixels(
    pixels,
    0,
    width,
    0,
    0,
    width,
    height
  )
  return bitmap
}


fun syncCreate(
  context: Context,
  name: String,
  bitmap: Bitmap
): File {
  val file = File(
    context.filesDir,
    "barcodes/$name.jpg"
  )

  file.outputStream().use { outputStream ->
    bitmap.compress(
      Bitmap.CompressFormat.JPEG,
      100,
      outputStream
    )
  }

  return file
}

fun syncEdit(
  context: Context,
  oldFile: File,
  name: String,
  bitmap: Bitmap
): File {
  val newFile = syncCreate(context, name, bitmap)
  oldFile.renameTo(newFile)
  return newFile
}

fun syncDelete(file: File) {
  file.delete()
}
