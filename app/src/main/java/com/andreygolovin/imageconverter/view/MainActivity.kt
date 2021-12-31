package com.andreygolovin.imageconverter.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.andreygolovin.imageconverter.databinding.ActivityMainBinding
import com.andreygolovin.imageconverter.presenter.MainPresenter
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    private val presenter by moxyPresenter { MainPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonConverter.setOnClickListener {
            convertJPGToPNG()
        }
    }

    private val getConverterResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.imageFromStorage.setImageURI(it.data?.data)
                presenter.setContext(this)
                presenter.saveJPGImageAsPNG(it.data!!)
            }
        }

    private fun convertJPGToPNG() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = INTENT_TYPE
            getConverterResult.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    convertJPGToPNG()
                } else {
                    Toast.makeText(this, PERMISSION_DENIED, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun directoryPath(path: String) {
        binding.saveImagePathTextView.text = path
    }

    companion object {
        private const val PERMISSION_CODE = 1001
        private const val INTENT_TYPE = "image/*"
        private const val PERMISSION_DENIED = "Permission denied"
    }
}