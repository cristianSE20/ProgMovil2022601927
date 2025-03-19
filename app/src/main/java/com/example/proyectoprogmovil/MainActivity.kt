package com.example.proyectoprogmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoprogmovil.ui.theme.ProyectoProgMovilTheme
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sensorHelper = SensorManagerHelper(this)


        setContent {
            Espada(
                sensorHelper = sensorHelper,
                onReset = { restartActivity() } // Pasamos la función onReset
            )

        }
    }
    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}

@Composable
fun Espada(sensorHelper: SensorManagerHelper, onReset: () -> Unit) {

    val context = LocalContext.current
    var currentImage by remember { mutableStateOf(R.drawable.empunadura) } // Imagen inicial

    val soundPool = remember { SoundPool.Builder().setMaxStreams(2).build() }
    var activationSoundId by remember { mutableStateOf(0) }
    var attackSoundId1 by remember { mutableStateOf(0) }
    var attackSoundId2 by remember { mutableStateOf(0) }
    var attackSoundId3 by remember { mutableStateOf(0) }
    var lastAttackTime by remember { mutableStateOf(0L) }
    var lastAttackTimeY by remember { mutableStateOf(0L) }
    val vibrator = remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    LaunchedEffect(Unit) {
        activationSoundId = soundPool.load(context, R.raw.activacion, 1)
        attackSoundId1 = soundPool.load(context, R.raw.golpe1, 1) // Sonido de ataque 1
        attackSoundId2 = soundPool.load(context, R.raw.golpe2, 1) // Sonido de ataque 2
        attackSoundId3 = soundPool.load(context, R.raw.golpe3, 1)
    }

    LaunchedEffect(sensorHelper.isSwordActive) {
        currentImage = if (sensorHelper.isSwordActive) {
            soundPool.play(activationSoundId, 2f, 2f, 0, 0, 1f)
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(200) // Vibra durante 200 ms
            }
            R.drawable.espada // Imagen con la hoja activa
        } else {
            R.drawable.empunadura // Imagen del mango
        }
    }

    LaunchedEffect(sensorHelper.isAttacking) {
        if (sensorHelper.isAttacking && sensorHelper.isSwordActive) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastAttack = currentTime - lastAttackTime

            // Esperar al menos 500 ms antes de reproducir el siguiente sonido
            if (timeSinceLastAttack >= 1000) {
                // Seleccionar un sonido de ataque aleatorio
                val attackSoundId = if (Random.nextBoolean()) attackSoundId1 else attackSoundId2
                soundPool.play(attackSoundId, 1.5f, 1.5f, 0, 0, 1f)

                // Actualizar el tiempo del último ataque
                lastAttackTime = currentTime

            }
        }
    }

    LaunchedEffect(sensorHelper.isAttackingY) {
        if (sensorHelper.isAttackingY && sensorHelper.isSwordActive) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastAttack = currentTime - lastAttackTimeY

            // Esperar al menos 500 ms antes de reproducir el siguiente sonido
            if (timeSinceLastAttack >= 1000) {
                // Reproducir el sonido de ataque específico para el eje Y
                soundPool.play(attackSoundId3, 1.5f, 1.5f, 0, 0, 1f)

                // Actualizar el tiempo del último ataque
                lastAttackTimeY = currentTime
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = currentImage), // Reemplaza con tu imagen
            contentDescription = "Espada de energía",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Para que ocupe toda la pantalla
        )

        IconButton(
            onClick = onReset, // Reiniciar la actividad
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .size(48.dp), // Tamaño pequeño
            content = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert), // Ícono de reinicio
                    contentDescription = "Reiniciar",
                    tint = androidx.compose.ui.graphics.Color.Transparent // Botón transparente
                )
            }
        )
    }


    LaunchedEffect(Unit) {
        sensorHelper.startListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorHelper.stopListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Acelerómetro:")
        Text(text = "Estado de la espada: ${if (sensorHelper.isSwordActive) "Activada" else "Desactivada"}")
        Text(text = "X: ${sensorHelper.accelerationX}")
        Text(text = "Y: ${sensorHelper.accelerationY}")
        Text(text = "Z: ${sensorHelper.accelerationZ}")
    }
}

class SensorManagerHelper(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var accelerationX by mutableStateOf(0f)
        private set
    var accelerationY by mutableStateOf(0f)
        private set
    var accelerationZ by mutableStateOf(0f)
        private set

    var isSwordActive by mutableStateOf(false)  // Estado de la espada
    var isAttacking by mutableStateOf(false)
    var isAttackingY by mutableStateOf(false)

    private val activationThreshold = 20f  // Sensibilidad gesto de activación (eje Z)
    private val attackThreshold = 40f      // Sensibilidad gesto de ataque (eje X)
    private val attackThresholdY =40f     // Sensibilidad gesto de ataque en el eje Y

    private var activationTime by mutableStateOf(0L)


    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            accelerationX = it.values[0]
            accelerationY = it.values[1]
            accelerationZ = it.values[2]

            // Detectar ACTIVACIÓN (empujón hacia adelante en eje X)
            if (!isSwordActive && accelerationX > activationThreshold) {
                isSwordActive = true
                activationTime = System.currentTimeMillis() // Registrar el momento de la activación
            }

            if (!isSwordActive && abs(accelerationY) > activationThreshold) {
                isSwordActive = true
                activationTime = System.currentTimeMillis() // Registrar el momento de la activación
            }

            val currentTime = System.currentTimeMillis()
            val timeSinceActivation = currentTime - activationTime

            isAttacking = isSwordActive && abs(accelerationZ) > attackThreshold && timeSinceActivation >= 2000

            isAttackingY = isSwordActive && abs(accelerationY) > attackThresholdY && timeSinceActivation >= 2000
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}