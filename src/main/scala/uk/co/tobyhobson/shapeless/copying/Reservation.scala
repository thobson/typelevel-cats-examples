package uk.co.tobyhobson.shapeless.copying

import java.time.LocalDate

case class Reservation(
  pickup: String,
  dropOff: String,
  from: LocalDate,
  to: LocalDate,
  vehicleCategory: String,
  automatic: Boolean,
  numDoors: Int,
  driverAge: Int,
  nationality: String
)
