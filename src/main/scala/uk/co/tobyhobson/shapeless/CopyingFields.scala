package uk.co.tobyhobson.shapeless

object CopyingFields {

  import shapeless.ops.hlist.Align
  import shapeless._
  import shapeless.syntax.singleton._

  def merge(location: Location, vehicle: Vehicle, driver: Driver, reservationConfirmed: Boolean): Reservation = {
    val locationRepr = LabelledGeneric[Location].to(location)
    val vehicleRepr = LabelledGeneric[Vehicle].to(vehicle)
    val driverRepr = LabelledGeneric[Driver].to(driver)
    // create a boolean tagged with 'reservationConfirmed
    val reservationConfirmedRepr = ('reservationConfirmed ->> false)
    // cat them to create an hlist of Records
    val unalignedHlist = locationRepr ++ vehicleRepr ++ driverRepr :+ reservationConfirmedRepr
    // align them
    val alignedHlist = /*_*/ align(unalignedHlist) /*_*/
    // convert the aligned hlist to a reservation
    LabelledGeneric[Reservation].from(alignedHlist)
  }

  def align[B, Unaligned <: HList, BRepr <: HList](a: Unaligned)(implicit ev: Align[Unaligned, BRepr]): BRepr = ev(a)

}
