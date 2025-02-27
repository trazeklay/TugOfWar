package fr.clemtrompier.cpe.tugofwar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.clemtrompier.cpe.tugofwar.ui.theme.TugOfWarTheme
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TugOfWarTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Hoist the state for IP and port here
    var ipValue by remember { mutableStateOf("") }
    var portValue by remember { mutableStateOf("") }
    var player by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Composable for setting the IP and port
        IPInputSection(
            ip = ipValue,
            port = portValue,
            onIpChange = { ipValue = it },
            onPortChange = { portValue = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Another composable that receives the IP and port values
        UDPActionSection(
            ip = ipValue,
            port = portValue,
            player = player
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerSelector(
            player = player,
            onPlayerChange = { player = it }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    TugOfWarTheme {
        MainScreen()
    }
}

@Composable
fun IPInputSection(
    ip: String,
    port: String,
    onIpChange: (String) -> Unit,
    onPortChange: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Input field for the IP address
        OutlinedTextField(
            value = ip,
            onValueChange = onIpChange,
            label = { Text("Adresse IP") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Input field for the port
        OutlinedTextField(
            value = port,
            onValueChange = onPortChange,
            label = { Text("Port") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun UDPActionSection(
    ip: String,
    port: String,
    player: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Utilisation de l'adresse IP : $ip et du port : $port avec le joueur $player")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                Log.d("UDPActionSection", "Sending UDP message")
                // Only send a UDP message if both IP and port are provided.
                if (ip.isNotBlank() && port.isNotBlank()) {
                    // Run the network operation on a background thread.
                    Thread {
                        sendUDPMessage(ip, port, "($player)")
                    }.start()
                }
            }
        ) {
            Text("Envoyer UDP")
        }
    }
}

fun sendUDPMessage(ip: String, port: String, message: String) {
    try {
        val portNumber = port.toInt()
        val socket = DatagramSocket()
        val buffer = message.toByteArray()
        val address = InetAddress.getByName(ip)
        val packet = DatagramPacket(buffer, buffer.size, address, portNumber)
        socket.send(packet)
        socket.close()
        println("Message sent to $ip:$portNumber")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun PlayerSelector(
    player: Int,
    onPlayerChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        PlayerIcon(
            player = "Player 1",
            playerValue = 1,
            isSelected = player == 1,
            onPlayerChange = onPlayerChange
        )
        Spacer(modifier = Modifier.width(16.dp))
        PlayerIcon(
            player = "Player 2",
            playerValue = 2,
            isSelected = player == 2,
            onPlayerChange = onPlayerChange
        )
    }
        Button(onClick = { onPlayerChange(0) }) {
            Text("Reset Player")
        } }

}

@Composable
fun PlayerIcon(
    player: String,
    playerValue: Int,
    isSelected: Boolean,
    onPlayerChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onPlayerChange(playerValue) }
            .background(if (isSelected) Color.Gray else Color.Transparent)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(text = player)
    }
}
