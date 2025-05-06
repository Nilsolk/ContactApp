package ru.nilsolk.contactapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.nilsolk.contactapp.databinding.ActivityMainBinding
import ru.nilsolk.contactapp.ui.ContactFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isReadGranted = permissions[Manifest.permission.READ_CONTACTS] == true
        val isWriteGranted = permissions[Manifest.permission.WRITE_CONTACTS] == true

        if (isReadGranted && isWriteGranted) {
            showContactFragment()
        } else {
            Toast.makeText(this, getString(R.string.need_permissions), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
    }

    private fun checkPermissions() {
        val isReadGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val isWriteGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (isReadGranted && isWriteGranted) {
            showContactFragment()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
            )
        }
    }

    private fun showContactFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ContactFragment())
            .commit()
    }
}

