package com.dicoding.nutriscan.camera

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.nutriscan.R
import com.dicoding.nutriscan.databinding.ActivityCameraOpenBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraOpenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraOpenBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var loadingDialog: AlertDialog? = null
    private lateinit var objectDetector: ObjectDetector
    private lateinit var database: DatabaseReference

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraOpenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menambahkan fungsi untuk tombol cancel
        binding.btnCancel.setOnClickListener {
            onCancelPressed()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        objectDetector = ObjectDetector(this, "model_densenet.tflite")
        val firebase = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = firebase.getReference("plants")

        binding.captureButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin tidak diberikan oleh pengguna.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Gagal menampilkan kamera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: filesDir
    }

    private fun takePhoto() {
        binding.progressBar.visibility = View.VISIBLE
        showLoadingDialog()

        val imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                binding.progressBar.visibility = View.GONE
                dismissLoadingDialog()
                Log.e(TAG, "Pengambilan foto gagal: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                binding.progressBar.visibility = View.GONE
                dismissLoadingDialog()

                stopCameraPreview()

                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val detectionResult = objectDetector.recognizeImage(bitmap)

                if (detectionResult.second >= 0.7) {
                    showBottomSheet(savedUri, detectionResult)

                    // Menyimpan hasil klasifikasi ke Firebase
                    saveToFirebase(savedUri, detectionResult)
                } else {
                    showErrorAkurasiDialog()
                }
            }
        })
    }

    private fun saveToFirebase(imageUri: Uri, detectionResult: Pair<String, Float>) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val plantName = detectionResult.first
            val plantRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("plants").child(plantName)

            // Ambil data dari Firebase berdasarkan nama objek
            plantRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val karbo = snapshot.child("karbo").getValue(Double::class.java) ?: 0.0
                    val serat = snapshot.child("serat").getValue(Double::class.java) ?: 0.0
                    val deskripsi = snapshot.child("deskripsi").getValue(String::class.java) ?: "Deskripsi tidak tersedia"
                    val kategori = snapshot.child("kategori").getValue(String::class.java) ?: "Tidak Diketahui"

                    val result = hashMapOf(
                        "nama" to plantName,
                        "imageUri" to imageUri.toString(),
                        "akurasi" to detectionResult.second, // Menyimpan nilai akurasi
                        "karbo" to karbo,
                        "serat" to serat,
                        "kategori" to kategori,
                        "deskripsi" to deskripsi
                    )

                    // Menyimpan data ke Firebase berdasarkan UID pengguna
                    val historyRef = FirebaseDatabase.getInstance("https://login-dan-register-8e341-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userId).child("history").child(plantName)
                    historyRef.setValue(result).addOnSuccessListener {
                        Log.d(TAG, "Hasil berhasil disimpan ke Firebase untuk pengguna dengan UID: $userId")
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error saat menyimpan hasil ke Firebase: ${e.message}", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error saat mengambil data tanaman dari Firebase: ${error.message}")
                }
            })
        } else {
            Log.e(TAG, "Pengguna belum login.")
        }
    }

    private fun onCancelPressed() {
        // Fungsi untuk handle cancel action
        finish()  // Menutup activity dan kembali ke activity sebelumnya
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(this)
            .setMessage("Sedang Memproses Gambar...")
            .setCancelable(false)
            .create()
        builder.show()

        loadingDialog = builder
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun stopCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun showErrorAkurasiDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setCancelable(false)
            setTitle("Akurasi Rendah")
            setMessage("Tidak dapat mengenali gambar \nCoba Ambil Gambar Lagi")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                startCamera()
            }
            create()
            show()
        }
    }

    private fun showBottomSheet(imageUri: Uri, detectionResult: Pair<String, Float>) {
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.activity_camera_result)
        bottomSheetDialog.findViewById<ImageView>(R.id.capture_image)?.setImageURI(imageUri)

        val label = detectionResult.first
        bottomSheetDialog.findViewById<TextView>(R.id.tv_nama_objek)?.text = label

        val probability = detectionResult.second
        val tvAkurasi = bottomSheetDialog.findViewById<TextView>(R.id.tv_akurasi)
        AdjustTextAkurasi(tvAkurasi, probability)

        val labelRef = database.child(label)
        setUpDataByLabel(labelRef, bottomSheetDialog)

        bottomSheetDialog.setOnDismissListener {
            startCamera()
        }

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }

    private fun setUpDataByLabel(labelRef: DatabaseReference, bottomSheetDialog: BottomSheetDialog) {
        labelRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kategori = snapshot.child("kategori").getValue(String::class.java)
                val tvKategori = bottomSheetDialog.findViewById<TextView>(R.id.tv_kelas)
                tvKategori?.text = kategori
                AdjustTextKategori(tvKategori, kategori)

                val nilaiKarbo = snapshot.child("karbo").getValue(Double::class.java)
                val tvKarbo = bottomSheetDialog.findViewById<TextView>(R.id.tv_nilai_karbo)
                val pbKarbo = bottomSheetDialog.findViewById<ProgressBar>(R.id.pb_karbo)

                if (nilaiKarbo != null) {
                    tvKarbo?.text = "$nilaiKarbo gram"
                    val nilaiPbKarbo = nilaiKarbo * 10
                    setProgressBarKarbo(pbKarbo, nilaiPbKarbo)
                }

                val nilaiSerat = snapshot.child("serat").getValue(Double::class.java)
                val tvSerat = bottomSheetDialog.findViewById<TextView>(R.id.tv_nilai_serat)
                val pbSerat = bottomSheetDialog.findViewById<ProgressBar>(R.id.pb_serat)

                if (nilaiSerat != null) {
                    tvSerat?.text = "$nilaiSerat gram"
                    val nilaiPbSerat = nilaiSerat * 100
                    setProgressBarSerat(pbSerat, nilaiPbSerat)
                }

                val deskripsi = snapshot.child("deskripsi").getValue(String::class.java)
                val tvDeskripsi = bottomSheetDialog.findViewById<TextView>(R.id.tv_keterangan)
                tvDeskripsi?.text = deskripsi ?: "Deskripsi tidak tersedia"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CameraOpenActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun AdjustTextAkurasi(tvAkurasi: TextView?, probability: Float) {
        val formattedProbability = String.format("%.1f", probability * 100)
        if (probability >= 0.8) {
            tvAkurasi?.text = "Kecocokan : $formattedProbability%"
            tvAkurasi?.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvAkurasi?.setTextSize(18f)
        } else if (probability >= 0.7 && probability < 0.8) {
            tvAkurasi?.text = "Kecocokan : $formattedProbability%"
            tvAkurasi?.setTextColor(ContextCompat.getColor(this, R.color.oranye))
            tvAkurasi?.setTextSize(18f)
        }
    }

    private fun AdjustTextKategori(tvKategori: TextView?, kategori: String?) {
        if (kategori == "Tinggi Serat" || kategori == "Tinggi Karbohidrat" || kategori == "Tinggi Karbohidrat dan Serat") {
            tvKategori?.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            tvKategori?.setTextColor(ContextCompat.getColor(this, R.color.merah))
        }
    }

    private fun setProgressBarKarbo(pbKarbo: ProgressBar?, value: Double?) {
        val max = 370
        pbKarbo?.max = max

        if (value!! / max >= 0.5) {
            pbKarbo!!.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbKarbo!!.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        ObjectAnimator.ofInt(pbKarbo, "progress", value.toInt())
            .setDuration(2000)
            .start()
    }

    private fun setProgressBarSerat(pbSerat: ProgressBar?, value: Double?) {
        val max = 360
        pbSerat?.max = max

        if (value!! / max >= 0.5) {
            pbSerat!!.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_green)
        } else {
            pbSerat!!.progressDrawable = ContextCompat.getDrawable(this, R.drawable.layer_red)
        }

        ObjectAnimator.ofInt(pbSerat, "progress", value.toInt())
            .setDuration(2000)
            .start()
    }
}
