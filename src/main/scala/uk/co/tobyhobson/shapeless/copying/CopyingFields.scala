package uk.co.tobyhobson.shapeless.copying

import java.time.LocalDate

object CopyingFields {

  import shapeless._

  def main(args: Array[String]): Unit = {
    val location = Location(pickup = "Malaga", dropOff = "Barcelona", from = LocalDate.of(2019,3,1), to = LocalDate.of(2019,3,10))
    val vehicle = Vehicle(vehicleCategory = "Compact", automatic = true, numDoors = 5)
    val driver = Driver(driverAge = 25, nationality = "British")
    val reservation = merge(location, vehicle, driver)
    println(reservation)
  }

  def merge(location: Location, vehicle: Vehicle, driver: Driver): Reservation = {
    val locationRepr = LabelledGeneric[Location].to(location)
    val vehicleRepr = LabelledGeneric[Vehicle].to(vehicle)
    val driverRepr = LabelledGeneric[Driver].to(driver)
    // cat them to create an hlist of Records
    val reservationRepr = locationRepr ++ vehicleRepr ++ driverRepr
    LabelledGeneric[Reservation].from(reservationRepr)
  }

}
