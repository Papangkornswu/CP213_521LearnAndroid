package com.example.a521lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var str by remember { mutableStateOf(10) }
            var agi by remember { mutableStateOf(10) }
            var intStat by remember { mutableStateOf(10) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
                    .padding(32.dp)
            ) {

                // HP Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(Color.White)
                ) {
                    Text(
                        text = "HP",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.521f)
                            .background(Color.Red)
                            .padding(8.dp)
                    )
                }

                // Image
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )

                // Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // STR
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { str++ }) {
                            Text("+", fontSize = 32.sp)
                        }
                        Text("Str", fontSize = 24.sp)
                        Text(str.toString(), fontSize = 24.sp)
                        Button(onClick = { str-- }) {
                            Text("-", fontSize = 32.sp)
                        }
                    }

                    // AGI
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { agi++ }) {
                            Text("+", fontSize = 32.sp)
                        }
                        Text("Agi", fontSize = 24.sp)
                        Text(agi.toString(), fontSize = 24.sp)
                        Button(onClick = { agi-- }) {
                            Text("-", fontSize = 32.sp)
                        }
                    }

                    // INT
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { intStat++ }) {
                            Text("+", fontSize = 32.sp)
                        }
                        Text("Int", fontSize = 24.sp)
                        Text(intStat.toString(), fontSize = 24.sp)
                        Button(onClick = { intStat-- }) {
                            Text("-", fontSize = 32.sp)
                        }
                    }
                }
            }
        }
    }
}
