package com.ethan.android.notepad.ui.technique.view

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ethan.android.notepad.R
import com.ethan.android.notepad.databinding.ActivityBlurHashBinding
import com.ethan.maskload.BlurHash

class BlurHashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBlurHashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sample image.
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.book)
        binding.original.setImageBitmap(bitmap)
        binding.original.layoutParams.height = bitmap.height
        binding.original.layoutParams.width = bitmap.width

        // Blur hashing.
        val blurHash = BlurHash.encode(bitmap, componentX = 5, componentY = 4)
        binding.blurHash.text = blurHash

        // Create blurred version.
        // We don't need to create a Bitmap in its full size.
        // Let Android scale it up for us as scaling is cheaper than generating a larger image.
        binding.blurred.setImageBitmap(
            BlurHash.decode(
                blurHash = blurHash,
                width = bitmap.width / 4,
                height = bitmap.height / 4,
            ),
        )
        binding.blurred.layoutParams.height = bitmap.height
        binding.blurred.layoutParams.width = bitmap.width
    }
}