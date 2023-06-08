package com.example.studymate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Environment
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.atwa.filepicker.core.FilePicker
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import io.appwrite.ID
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@Suppress("DEPRECATION")
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
    private val cameraPermissionRequestCode = 100
    private val imageCaptureRequestCode = 200
    private val imageFileName = "captured_image.jpg"
    private lateinit var filePicker: FilePicker
    private lateinit var tempFileId : String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        
        val v : View = inflater.inflate(R.layout.fragment_create_notes, container, false)
        preferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        if (!hasCameraPermission())
            requestCameraPermission()

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
            createCardText(toBeAdded, i, isOcr = false, isFile = false)
            i += 1
        }

        addFile.setOnClickListener {
            readAndUpdateDatabase(shouldUpdate = false, isFileUpdate = true)
            fileCount += 1
            filePicker.pickFile { selectedFile ->
                selectedFile?.let {
                    val filePath = it.file
                    val type = filePath.toString().takeLast(3)
                    val fileName: String? = it.name
                    if(type == "pdf"){
                        val extractedData =
                            extractData(filePath.toString()).replace(Regex("[\n\\s]+"), " ")
                        sentencesList.add(extractedData)
                    }
                    // Continue with the file processing logic
                    CoroutineScope(Dispatchers.Main).launch {
                        val client = Client(requireContext())
                            .setEndpoint("https://cloud.appwrite.io/v1")
                            .setProject("64734c27ee025a6ee21c")
                        val storage = Storage(client)
                        try {
                            storage.createFile(
                                bucketId = "647d72e7564902ca8b17",
                                fileId = userId + fileCount.toString(),
                                file = InputFile.fromPath(filePath.toString()),
                            )
                            // Mention file creation
                            sentencesList.add(userId + fileCount.toString())
                            Toast.makeText(context, "File Uploaded!", Toast.LENGTH_SHORT).show()
                            createCardText(fileName.toString(), fileCount,
                                isOcr = false,
                                isFile = true
                            )
                            // Update changes to db
                            readAndUpdateDatabase(shouldUpdate = true, isFileUpdate = true)
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
            capturedImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                imageFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
            startActivityForResult(cameraIntent, imageCaptureRequestCode)
        }

        saveNotes.setOnClickListener {
            readAndUpdateDatabase(shouldUpdate = false, isFileUpdate = false)
            if (noteName.text.isEmpty()) {
                Toast.makeText(context, "Enter a Note Name to proceed", Toast.LENGTH_SHORT).show()
            } else {

                CoroutineScope(Dispatchers.Main).launch {
                    val client = Client(requireContext())
                        .setEndpoint("https://cloud.appwrite.io/v1")
                        .setProject("64734c27ee025a6ee21c")
                    val databases = Databases(client)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = Date()
                    try {
                        databases.createDocument(
                            databaseId = "6479d563804822fc79bb",
                            collectionId = "6479f9af8834a056c20d",
                            documentId = ID.unique(),
                            data = mapOf(
                                    "session-id" to sessionId,
                                    "user-id" to userId,
                                    "note-name" to noteName.text.toString(),
                                    "note-text" to sentencesList,
                                    "date-created" to dateFormat.format(currentDate)
                                )
                        )
                        Toast.makeText(context, "Note Created!", Toast.LENGTH_SHORT).show()
                        sentencesList = mutableListOf()
                        i = 0
                        countOcr = 0
                        noteName.setText("")
                        displayNotes.removeAllViews()
                        // Update changes to db
                        readAndUpdateDatabase(shouldUpdate = true, isFileUpdate = false)
                        Log.d("count", "$docCount")
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun readAndUpdateDatabase(shouldUpdate :  Boolean, isFileUpdate : Boolean) {
        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("64734c27ee025a6ee21c")

        val database = Databases(client)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if(!shouldUpdate){
                    val response = database.getDocument(
                        databaseId = "648081c3025f25473245",
                        collectionId = "6480820466d1d4790f90",
                        documentId = userId,
                    )
                    docCount = (response.data["notes"] as Long).toInt()
                    fileCount = (response.data["files"] as Long).toInt()
                }
                else{
                    if(isFileUpdate)
                        fileCount += 1
                    else
                        docCount += 1
                    database.updateDocument(
                        databaseId = "648081c3025f25473245",
                        collectionId = "6480820466d1d4790f90",
                        documentId = userId,
                        data = mapOf(
                            "files" to fileCount,
                            "notes" to docCount,
                        )
                    )
                }
                // Further flow for the logged-in user
            } catch (e: Exception) {
                // Handle login failure
                Toast.makeText(context, "Could not fetch data", Toast.LENGTH_SHORT).show()
                Log.d("Error", "$e")
            }
        }
    }

    @SuppressLint("SetTextI18n")
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
            textView.text = "File ${count}: $toBeAdded"
        else
            textView.text = "Note ${count}:"
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat)
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

    private fun createImageFile(): File {
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFileName)
    }
    private fun hasCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageCaptureRequestCode && resultCode == Activity.RESULT_OK) {
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
        TextRecognition.getClient(recognizerOptions)
        val updatedRecognizer = TextRecognition.getClient(recognizerOptions)
        updatedRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                var recognizedText = visionText.text
                // Handle the recognized text as desired
                recognizedText = recognizedText.replace("\n", ". ")
                createCardText(recognizedText, countOcr, isOcr = true, isFile = false)
                sentencesList.add(recognizedText)
                countOcr += 1
            }
            .addOnFailureListener { exception ->
                // Handle the OCR failure
                exception.printStackTrace()
            }
    }
    private fun extractData(path : String): String {
        var extractedText = ""
        try {
            val pdfReader = PdfReader(path)
            val n = pdfReader.numberOfPages

            for (i in 0 until n) {
                extractedText =
                    """
                 $extractedText${
                        PdfTextExtractor.getTextFromPage(pdfReader, i + 1).trim { it <= ' ' }
                    }
                  
                 """.trimIndent()
            }
            pdfReader.close()
            return extractedText
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    return extractedText
    }
}
