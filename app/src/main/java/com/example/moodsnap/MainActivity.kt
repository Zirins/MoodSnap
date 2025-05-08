package com.example.moodsnap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var imageViewSelfie: ImageView
    private lateinit var takeSelfieButton: Button
    private lateinit var moodGroup: RadioGroup
    private lateinit var editTextNote: EditText
    private lateinit var saveMoodButton: Button

    // The database
    private lateinit var db: MoodDatabase

    // Camera request codes
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 101

    // To retain image and path across state changes
    private var selfieBitmap: Bitmap? = null
    private var lastCapturedImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewSelfie = findViewById(R.id.imageViewSelfie)
        takeSelfieButton = findViewById(R.id.btnTakeSelfie)
        moodGroup = findViewById(R.id.radioGroupMood)
        editTextNote = findViewById(R.id.editTextNote)
        saveMoodButton = findViewById(R.id.btnSaveMood)
        db = MoodDatabase.getDatabase(this)

        // Restore our state after rotation!
        if (savedInstanceState != null) {
            editTextNote.setText(savedInstanceState.getString("note"))
            val moodId = savedInstanceState.getInt("selectedMoodId", -1)
            if (moodId != -1) moodGroup.check(moodId)
            selfieBitmap = savedInstanceState.getParcelable("selfie")
            selfieBitmap?.let {
                imageViewSelfie.setImageBitmap(it)
            }
            lastCapturedImagePath = savedInstanceState.getString("imagePath")
        }

        // Launch the camera to take selfie
        takeSelfieButton.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        // Save mood entry to database
        saveMoodButton.setOnClickListener {
            val selectedId = moodGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Pick a mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMood = findViewById<RadioButton>(selectedId).text.toString()
            val noteText = editTextNote.text.toString()

            val moodEntry = MoodEntry(
                mood = selectedMood,
                note = noteText,
                imagePath = lastCapturedImagePath
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.moodDao().insertMood(moodEntry)
            }

            Toast.makeText(this, "Mood saved, boss", Toast.LENGTH_SHORT).show()
        }

        // View mood history screen
        findViewById<Button>(R.id.btnViewHistory).setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }
    }

    // Check camera permission and request if not granted
    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )

        } else {
            launchCamera()
        }
    }


    // Launch camera
    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    // Request camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission denied, bruh.", Toast.LENGTH_SHORT).show()
        }
    }


    // Handle result from camera and save image locally
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                imageViewSelfie.setImageBitmap(it)
                selfieBitmap = it

                // Save image to internal storage
                val filename = "selfie_${System.currentTimeMillis()}.jpg"
                val file = File(filesDir, filename)
                val outputStream = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                lastCapturedImagePath = file.absolutePath
            }
        }
    }

    // Save view state across screen rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("note", editTextNote.text.toString())
        outState.putInt("selectedMoodId", moodGroup.checkedRadioButtonId)
        outState.putParcelable("selfie", selfieBitmap)
        outState.putString("imagePath", lastCapturedImagePath)
    }
}
