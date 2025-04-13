package com.example.parks.data

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@Composable
fun MapView(latitud: String, longitud: String, modifier: Modifier = Modifier) {
    val lat = latitud.toDoubleOrNull() ?: 0.0
    val long = longitud.toDoubleOrNull() ?: 0.0

    if (lat == 0.0 && long == 0.0){
        Text("Ubicación no dispobible", color = Color.Red)
    } else{
        AndroidView(
            factory = { context ->
                val mapView = org.osmdroid.views.MapView(context).apply {
                    Configuration.getInstance().userAgentValue = context.packageName
                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setZoom(18.0)  // Zoom más cercano para mayor detalle
                    setMultiTouchControls(true)
                    isTilesScaledToDpi = true

                    val geoPoint = GeoPoint(lat, long)
                    controller.setCenter(geoPoint)

                    // Marcador predeterminado (sin ícono personalizado)
                    Marker(this).apply {
                        position = geoPoint
                        title = "Ubicación del parque"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)  // Centra el marcador
                    }.also { overlays.add(it) }
                }
                mapView
            },
            modifier = Modifier
                .height(260.dp)  // Altura ajustada para integrarse mejor
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

