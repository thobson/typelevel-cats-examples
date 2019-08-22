package uk.co.tobyhobson.shapeless.copyingTwo

import java.time.LocalDate

object CopyingFields {

  import shapeless._
  import shapeless.record._
  import shapeless.syntax.singleton._
  import ShapelessOps._

  def main(args: Array[String]): Unit = {
    val location = Location(dropOff = Null, pickup = "Malaga", from = LocalDate.of(2019,3,1), to = LocalDate.of(2019,3,10))
    val vehicle = Vehicle(vehicleCategory = "Compact", automatic = true, numDoors = 5, isDiesel = true)
    val driver = Driver(driverAge = 25, nationality = "British")
    val reservation = merge(location, vehicle, driver)
    println(reservation)
  }

  def merge(location: Location, vehicle: Vehicle, driver: Driver): Reservation = {
    val LocationGen = LabelledGeneric[Location]
    val VehicleGen = LabelledGeneric[Vehicle]
    val DriverGen = LabelledGeneric[Driver]

    val locationRepr = LocationGen.to(location)
    val (_, vehicleRepr) = VehicleGen.to(vehicle).remove('isDiesel)
    val driverRepr = DriverGen.to(driver)

    // cat them to create an hlist of Records
    val reservationRepr = locationRepr ++ vehicleRepr ++ driverRepr :+ ('confirmed ->> true)
    /*_*/ reservationRepr.as[Reservation] /*_*/
  }

}
