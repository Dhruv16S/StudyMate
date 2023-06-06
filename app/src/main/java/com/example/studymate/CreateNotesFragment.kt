package com.example.studymate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.Manifest
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.atwa.filepicker.core.FilePicker

class CreateNotesFragment : Fragment() {

    private lateinit var addText : Button
    private lateinit var addFile : Button
    private lateinit var addOcr : Button
    private lateinit var saveNotes : Button
    private lateinit var scroll : ScrollView
    private lateinit var displayNotes : LinearLayout
    private lateinit var noteName : EditText
    private lateinit var noteContent : EditText
    private lateinit var preferences: SharedPreferences
    private lateinit var userId : String
    private lateinit var sessionId : String
    private var docCount : Int = 0
    private var fileCount : Int = 0
    private var capturedImageUri: Uri? = null
    private var sentencesList = mutableListOf<String>()
    private var i : Int = 1
    private var countOcr : Int = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val IMAGE_CAPTURE_REQUEST_CODE = 200
    private val IMAGE_FILE_NAME = "captured_image.jpg"
    private val FILE_CHOOSER_REQUEST_CODE = 300
    private lateinit var filePicker: FilePicker
    private lateinit var filePath :File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        
        val v : View = inflater.inflate(R.layout.fragment_create_notes, container, false)

        if (hasCameraPermission()) {
            // Start the camera
        } else {
            // Request camera permission
            requestCameraPermission()
        }

        addText = v.findViewById(R.id.addText)
        addFile = v.findViewById(R.id.addFile)
        addOcr = v.findViewById(R.id.addOcr)
        saveNotes = v.findViewById(R.id.saveNotes)
        scroll = v.findViewById(R.id.scrollView)
        displayNotes = v.findViewById(R.id.displayNotes)
        noteName = v.findViewById(R.id.noteName)
        noteContent = v.findViewById(R.id.noteContent)
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        sessionId = preferences.getString("sessionId", " ").toString()
        userId = preferences.getString("userId", " ").toString()
        filePicker = FilePicker.getInstance(this)

        addText.setOnClickListener {
            val toBeAdded = noteContent.text.toString()

            if(toBeAdded.isEmpty()){
                Toast.makeText(context, "Enter some text: ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Create a new CardView
            sentencesList.add(toBeAdded)
            createCardText(toBeAdded, i, false, false)
            i += 1
        }

        addFile.setOnClickListener {
            fileCount += 1
            filePicker.pickFile { selectedFile ->
                selectedFile?.let {
                    val filePath = it.file
                    val fileName: String? = it.name
                    // Continue with the file processing logic
                    CoroutineScope(Dispatchers.Main).launch {
                        val client = Client(requireContext())
                            .setEndpoint("https://cloud.appwrite.io/v1")
                            .setProject("64734c27ee025a6ee21c")
                        val storage = Storage(client)
                        try {
                            val response = storage.createFile(
                                bucketId = "647d72e7564902ca8b17",
                                fileId = sessionId + fileCount.toString(),
                                file = InputFile.fromPath(filePath.toString()),
                            )
                            // Mention file creation
                            Toast.makeText(context, "File Uploaded!", Toast.LENGTH_SHORT).show()
                            createCardText(fileName.toString(), fileCount, false, true)
                        } catch (e: Exception) {
                            Log.e("Appwrite", "Error: " + e.message)
                        }
                    }
                }
            }
        }


        addOcr.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Create a file to store the captured image in the app-specific directory
            val imageFile = createImageFile()
            if (imageFile != null) {
                capturedImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".fileprovider",
                    imageFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST_CODE)
            } else {
                Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show()
            }
        }

        saveNotes.setOnClickListener {
            docCount += 1
            if (noteName.text.isEmpty()) {
                Toast.makeText(context, "Enter a Note Name to proceed", Toast.LENGTH_SHORT).show()
            } else {

                CoroutineScope(Dispatchers.Main).launch {
                    val client = Client(requireContext())
                        .setEndpoint("https://cloud.appwrite.io/v1")
                        .setProject("64734c27ee025a6ee21c")
                    val databases = Databases(client)
                    try {
                        val response = databases.createDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "6479f9af8834a056c20d",
                            documentId = sessionId + docCount.toString(),
                            data = mapOf(
                                    "session-id" to sessionId,
                                    "user-id" to userId,
                                    "note-name" to noteName.text.toString(),
                                    "note-content" to sentencesList
                                )
                        )
                        Toast.makeText(context, "Note Created!", Toast.LENGTH_SHORT).show()
                        sentencesList = mutableListOf<String>()
                        i = 0
                        countOcr = 0
                        noteName.setText("")
                        displayNotes.removeAllViews()
                    } catch (e: Exception) {
                        Log.e("Appwrite", "Error: " + e.message)
                    }
                }
            }
        }

        v.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }

        return v
    }

    private fun createCardText(toBeAdded : String, count : Int, isOcr : Boolean, isFile : Boolean) {
        val cardView = CardView(requireContext())

        // Set CardView layout parameters
        val cardLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardLayoutParams.setMargins(10.0.dpToPx(), 5.0.dpToPx(), 10.0.dpToPx(), 5.0.dpToPx())
        cardView.layoutParams = cardLayoutParams
        cardView.cardElevation = 25.0.dpToPx().toFloat()
        cardView.setBackgroundResource(R.drawable.note_card_template)

        // Create a LinearLayout to hold the TextView and EditText
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx(), 16.0.dpToPx())

        // Create the TextView
        val textView = TextView(requireContext())
        if(isOcr)
            textView.text = "OCR Detected Note ${count}:"
        else if(isFile)
            textView.text = "File ${count}: ${toBeAdded}"
        else
            textView.text = "Note ${count}:"
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        textView.textSize = 16.0f
        textView.setTypeface(null, Typeface.BOLD)

        // Add the TextView to the LinearLayout
        linearLayout.addView(textView)

        if(!isFile){// Create the EditText
            val editText = EditText(requireContext())
            val editTextLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            editText.layoutParams = editTextLayoutParams
            editText.typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans)
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setBackgroundResource(0) // Removes the line associated with the EditText
            editText.setTextCursorDrawable(R.drawable.black_cursor)
            editText.setText(toBeAdded)
            editText.tag = "noteEditText"
            editText.textSize = 17.0f
            editText.setTypeface(null, Typeface.BOLD)

            // Add the EditText to the LinearLayout
            linearLayout.addView(editText)
        }
        // Add the LinearLayout to the CardView
        cardView.addView(linearLayout)
        // Add the CardView to the parent LinearLayout (displayNotes)
        displayNotes.addView(cardView)
        noteContent.setText("")
    }

    private fun Double.dpToPx(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun createImageFile(): File? {
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, IMAGE_FILE_NAME)
    }
    private fun hasCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = capturedImageUri // Use the captured image URI instead of data extras
            if (imageUri != null) {
                val imageBitmap = loadBitmapFromUri(imageUri)
                if (imageBitmap != null) {
                    val processedImageUri = saveImageAndGetUri(imageBitmap)
                    processedImageUri?.let { uri ->
                        processCapturedImage(uri)
                    }
                } else {
                    Log.d("URI", "Failed to load image bitmap from URI.")
                }
            } else {
                Log.d("URI", "Captured image URI is null.")
            }
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e("LoadBitmap", "Error loading image bitmap: ${e.message}")
            null
        }
    }

    private fun saveImageAndGetUri(imageBitmap: Bitmap): Uri? {
        val imagesDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        val file = File(imagesDirectory, fileName)
        return try {
            FileOutputStream(file).use { outputStream ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
            }
            FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".fileprovider", file)
        } catch (e: IOException) {
            Log.e("SaveImage", "Error saving image: ${e.message}")
            null
        }
    }

    private fun processCapturedImage(uri: Uri) {
        val image = InputImage.fromFilePath(requireContext(), uri)

        val recognizerOptions = TextRecognizerOptions.Builder()
            .build()
        val recognizer = TextRecognition.getClient(recognizerOptions)
        val updatedRecognizer = TextRecognition.getClient(recognizerOptions)
        updatedRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                var recognizedText = visionText.text
                // Handle the recognized text as desired
                recognizedText = recognizedText.replace("\n", ". ")
                createCardText(recognizedText, countOcr, true, false)
                sentencesList.add(recognizedText)
                countOcr += 1
            }
            .addOnFailureListener { exception ->
                // Handle the OCR failure
                exception.printStackTrace()
            }
    }
}
