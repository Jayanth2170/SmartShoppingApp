package com.example.smartshoppingcart





import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ai.onnxruntime.*
import com.google.common.util.concurrent.ListenableFuture
import java.io.ByteArrayOutputStream
import java.nio.FloatBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.compose.ui.graphics.Color  // Correct import for Compose Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable


class HomePageActivity : ComponentActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var ortSession: OrtSession
    private lateinit var ortEnv: OrtEnvironment

    private var detectedLabel = mutableStateOf("Click Scan to Shop")
    private var isDetected = mutableStateOf(false)

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private val cartItems = mutableStateMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        try {
            ortEnv = OrtEnvironment.getEnvironment()
            val modelBytes = assets.open("best_model.onnx").readBytes()
            ortSession = ortEnv.createSession(modelBytes)
        } catch (e: Exception) {
            Log.e("ONNX", "Failed to load model: ${e.message}")
        }

        setContent {
            MaterialTheme {
                var showCart by remember { mutableStateOf(false) }
                if (showCart) {
                    CartScreen(cartItems = cartItems) {
                        showCart = false
                    }
                } else {
                    HomePageScreen(cartItems = cartItems) {
                        showCart = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Camera permission granted.")
            }
        }
    }

    @Composable
    fun HomePageScreen(cartItems: MutableMap<String, Int>, onCartClick: () -> Unit) {
        val context = LocalContext.current
        val previewView = remember { PreviewView(context) }
        var isScanning by remember { mutableStateOf(false) }

        LaunchedEffect(isScanning) {
            if (isScanning) {
                startCamera(previewView)
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome to the Smart Shopping Cart!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Blue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show camera preview only when scanning is active
            if (isScanning) {
                AndroidView({ previewView }, modifier = Modifier.size(300.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display the detected label
            Text(
                text = detectedLabel.value,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDetected.value) Color.Green else Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Add item to cart only when an item is detected
            if (isDetected.value) {
                Button(
                    onClick = {
                        val item = detectedLabel.value.removePrefix("Detected: ").trim()
                        cartItems[item] = (cartItems[item] ?: 0) + 1
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Add to Cart")
                }
            }

            // Start scanning button
            Button(
                onClick = { isScanning = true },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Start Scanning Objects")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shopping cart button
            IconButton(
                onClick = onCartClick,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .background(Color.Blue, CircleShape)
                    .padding(12.dp)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color.White)
            }
        }
    }


    @Composable
    fun CartScreen(cartItems: MutableMap<String, Int>, onBack: () -> Unit) {
        var showTotal by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf<String?>(null) }

        val context = LocalContext.current
        val dbHelper = remember { DBHelper(context) }
        val db = dbHelper.writableDatabase // Get the SQLiteDatabase instance

        if (selectedItem != null) {
            ItemDetailsScreen(
                itemName = selectedItem!!,
                dbHelper = dbHelper,
                onBack = { selectedItem = null }
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your Cart", style = MaterialTheme.typography.titleLarge)

                if (cartItems.isEmpty()) {
                    Text("Cart is empty.")
                } else {
                    cartItems.forEach { (item, quantity) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedItem = item }, // Make the row clickable
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("$item x$quantity")
                                Row {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = {
                                        if (cartItems[item] == 1) cartItems.remove(item)
                                        else cartItems[item] = cartItems[item]!! - 1
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Remove")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { showTotal = true }) {
                        Text("Checkout")
                    }

                    if (showTotal) {
                        var total = 0
                        cartItems.forEach { (item, quantity) ->
                            val itemDetails =
                                dbHelper.getItemDetails(db, item) // Pass the db instance
                            total += (itemDetails?.price ?: 0) * quantity
                        }
                        Text(
                            "Your total bill is ₹$total",
                            color = Color.Blue,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Back to Home")
                }
            }
        }
    }


    @Composable
    fun ItemDetailsScreen(itemName: String, dbHelper: DBHelper, onBack: () -> Unit) {
        val db = dbHelper.writableDatabase // Get the SQLiteDatabase instance
        val itemDetails =
            dbHelper.getItemDetails(db, itemName) // Pass the database to getItemDetails

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Details", style = MaterialTheme.typography.titleLarge)

            itemDetails?.let {
                Text("Name: ${it.name}")
                Text("Price: ₹${it.price}")
                Text("Expiry: ${it.expiry}") // Use 'expiry' instead of 'expiryDate'
                if (it.calories != null && it.carbs != null) {
                    Text("Calories: ${it.calories}")
                    Text("Carbohydrates: ${it.carbs}")
                }
            } ?: run {
                Text("Details not found.")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back to Cart")
            }
        }
    }


    private fun startCamera(previewView: PreviewView) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("Camera", "Binding failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val bitmap = imageProxyToBitmap(imageProxy)
        if (bitmap != null) {
            try {
                val inputTensor = convertBitmapToTensor(bitmap)
                val inputName = ortSession.inputNames.iterator().next()
                val results = ortSession.run(mapOf(inputName to inputTensor))
                val output = results[0].value as Array<Array<FloatArray>>
                val detections = output[0]

                var bestConfidence = 0f
                var bestLabelIndex = -1

                for (detection in detections) {
                    val objectness = detection[4]
                    if (objectness > 0.3f) {
                        val classScores = detection.copyOfRange(5, detection.size)
                        val maxScore = classScores.maxOrNull() ?: 0f
                        val classIndex = classScores.indexOfFirst { it == maxScore }

                        if (maxScore > bestConfidence) {
                            bestConfidence = maxScore
                            bestLabelIndex = classIndex
                        }
                    }
                }

                if (bestLabelIndex != -1) {
                    val label = getClassLabel(bestLabelIndex)
                    detectedLabel.value = "Detected: $label"
                    isDetected.value = true
                } else {
                    detectedLabel.value = "No object detected"
                    isDetected.value = false
                }
            } catch (e: Exception) {
                detectedLabel.value = "Error during inference"
                isDetected.value = false
            }
        } else {
            detectedLabel.value = "Image processing error"
            isDetected.value = false
        }
        imageProxy.close()
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return try {
            val yBuffer = imageProxy.planes[0].buffer
            val uBuffer = imageProxy.planes[1].buffer
            val vBuffer = imageProxy.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage =
                YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
            BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
        } catch (e: Exception) {
            null
        }
    }

    private fun convertBitmapToTensor(bitmap: Bitmap): OnnxTensor {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val floatValues = FloatArray(3 * 640 * 640)
        val intValues = IntArray(640 * 640)
        resizedBitmap.getPixels(intValues, 0, 640, 0, 0, 640, 640)

        for (i in intValues.indices) {
            val pixel = intValues[i]
            floatValues[i] = ((pixel shr 16) and 0xFF) / 255.0f
            floatValues[i + 640 * 640] = ((pixel shr 8) and 0xFF) / 255.0f
            floatValues[i + 2 * 640 * 640] = (pixel and 0xFF) / 255.0f
        }

        return OnnxTensor.createTensor(
            ortEnv,
            FloatBuffer.wrap(floatValues),
            longArrayOf(1, 3, 640, 640)
        )
    }

    private fun getClassLabel(index: Int): String {
        val labels = listOf(
            "1 Dark Chocolate",
            "Ayurveda gel",
            "Balaji Aloo Sev",
            "Balaji Ratlam Sev",
            "Celebrations Pack",
            "Closeup",
            "Colgate",
            "Dabeli Masala",
            "Dabeli burger",
            "Dark Fantasy",
            "Dove Shampoo",
            "Dove soap",
            "Everest",
            "Garam Masala",
            "Head Shoulders Shampoo",
            "Krack Jack",
            "Liril",
            "Lux soap",
            "Malan",
            "Marie Gold",
            "Nescafe",
            "Real Grape",
            "Rin Big Bar",
            "TATA Salt",
            "Tomato Twist Lays",
            "Tresemme",
            "Undhiya",
            "Vasline Aloe",
            "Veg Hakka Noodles",
            "ViccoVajradant",
            "Vim soap",
            "White Lakme",
            "blue lays",
            "lifeboy soap",
            "maggie",
            "orange lays",
            "pears soap",
            "pr",
            "surf"
        )
        return labels.getOrElse(index) { "Unknown" }
    }
}

