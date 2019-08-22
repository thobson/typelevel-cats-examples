package uk.co.tobyhobson.shapeless.copyingTwo

object ShapelessOps {

  import shapeless._
  import shapeless.ops.hlist

  trait Aligner[A, B] {
    def apply(a: A): B
  }

  implicit def genericAligner[ARepr <: HList, B, BRepr <: HList](
    implicit
    bGen    : LabelledGeneric.Aux[B, BRepr],
    align   : hlist.Align[ARepr, BRepr]
  ): Aligner[ARepr, B] = new Aligner[ARepr, B] {
    def apply(a: ARepr): B = bGen.from(align.apply(a))
  }

  implicit class AlignerOps[ARepr <: HList](a: ARepr) {
    def as[B](implicit aligner: Aligner[ARepr, B]): B = aligner.apply(a)
  }

}