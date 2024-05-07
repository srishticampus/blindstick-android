package com.example.blindstick

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blindstick.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var dataBase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        binding.button.setOnClickListener{
//            freeSlotCount()
//        }

        binding.phamacyProgress.visibility = View.VISIBLE
        dataBaseListener()

    }

    private fun dataBaseListener() {

        dataBase = FirebaseDatabase.getInstance().reference
        val postListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                binding.phamacyProgress.visibility = View.GONE
                val lat = snapshot.child("lat:").value
                val long = snapshot.child("long:").value
                val heartRate = snapshot.child("hrate:").value
                val alert = snapshot.child("Alert").value

                if (alert.toString().equals("1")) {
                    try {
                        val notification =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(applicationContext, notification)
                        r.play()
                        val vibrator: Vibrator
                        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(3000)
                        binding.logo.setImageResource(R.drawable.sos_transparent)
                        binding.dangerText.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    binding.logo.setImageResource(R.drawable.smartstick)
                    binding.dangerText.visibility = View.GONE
                }

                binding.latitude.text = "Latitude : " + "$lat"
                binding.longitude.text = "Longitude : " + "$long"
                binding.heartRateValue.text = "Your Heart Rate is : " + "$heartRate"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to read sensor data", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        dataBase.addValueEventListener(postListener)
    }

    private fun freeSlotCount() {
        dataBase = FirebaseDatabase.getInstance().getReference("Alert")
        dataBase.setValue("0").addOnSuccessListener {
            //Toast.makeText(applicationContext, "Booked", Toast.LENGTH_SHORT).show()
        }
    }

}