package uk.co.tobyhobson.shapeless

import java.time.LocalDate

case class Location(pickup: String, dropOff: String, from: LocalDate, to: LocalDate)
