package com.matttax.drivebetter.map.presentation.mapkit

import com.matttax.drivebetter.map.domain.model.RidePoint
import com.matttax.drivebetter.map.presentation.states.MapViewState

actual object CameraManager {
    actual fun getPosition(ridePoint: RidePoint?, isDriving: Boolean): MapViewState {
        return if (ridePoint == null) {
            MapViewState(
                RUSSIA_CENTER_POINT,
                0.0f,
                0.0f,
                0.0f
            )
        } else if (!isDriving) {
            MapViewState(
                ridePoint.location,
                15.0f,
                0.0f,
                0.0f
            )
        } else {
            MapViewState(
                ridePoint.location,
                ridePoint.azimuth.azimuth.toFloat(),
                45.0f,
                25.0f
            )
        }
    }
}
