package scalan.spark.collections
package impl

import scala.annotation.unchecked.uncheckedVariance
import scalan.OverloadId
import scalan.common.Default
import scalan.common.OverloadHack.Overloaded1
import scalan.spark.{SparkDslExp, SparkDslSeq, SparkDsl}
import scala.reflect.runtime.universe._
import scalan.common.Default

// Abs -----------------------------------
trait RDDCollectionsAbs extends SparkDsl with RDDCollections {
  self: SparkDsl with RDDCollectionsDsl =>
  // single proxy for each type family
  implicit def proxyIRDDCollection[A](p: Rep[IRDDCollection[A]]): IRDDCollection[A] = {
    implicit val tag = weakTypeTag[IRDDCollection[A]]
    proxyOps[IRDDCollection[A]](p)(TagImplicits.typeTagToClassTag[IRDDCollection[A]])
  }

  abstract class IRDDCollectionElem[A, From, To <: IRDDCollection[A]](iso: Iso[From, To])(implicit eA: Elem[A])
    extends ViewElem[From, To](iso) {
    override def convert(x: Rep[Reifiable[_]]) = convertIRDDCollection(x.asRep[IRDDCollection[A]])
    def convertIRDDCollection(x : Rep[IRDDCollection[A]]): Rep[To]
  }

  trait IRDDCollectionCompanionElem extends CompanionElem[IRDDCollectionCompanionAbs]
  implicit lazy val IRDDCollectionCompanionElem: IRDDCollectionCompanionElem = new IRDDCollectionCompanionElem {
    lazy val tag = weakTypeTag[IRDDCollectionCompanionAbs]
    protected def getDefaultRep = IRDDCollection
  }

  abstract class IRDDCollectionCompanionAbs extends CompanionBase[IRDDCollectionCompanionAbs] with IRDDCollectionCompanion {
    override def toString = "IRDDCollection"
  }
  def IRDDCollection: Rep[IRDDCollectionCompanionAbs]
  implicit def proxyIRDDCollectionCompanion(p: Rep[IRDDCollectionCompanion]): IRDDCollectionCompanion = {
    proxyOps[IRDDCollectionCompanion](p)
  }

  // single proxy for each type family
  implicit def proxyIRDDPairCollection[A, B](p: Rep[IRDDPairCollection[A, B]]): IRDDPairCollection[A, B] = {
    implicit val tag = weakTypeTag[IRDDPairCollection[A, B]]
    proxyOps[IRDDPairCollection[A, B]](p)(TagImplicits.typeTagToClassTag[IRDDPairCollection[A, B]])
  }
  abstract class IRDDPairCollectionElem[A, B, From, To <: IRDDPairCollection[A, B]](iso: Iso[From, To])
    extends ViewElem[From, To](iso) {
    override def convert(x: Rep[Reifiable[_]]) = convertIRDDPairCollection(x.asRep[IRDDPairCollection[A, B]])
    def convertIRDDPairCollection(x : Rep[IRDDPairCollection[A, B]]): Rep[To]
  }

  // single proxy for each type family
  implicit def proxyIRDDNestedCollection[A](p: Rep[IRDDNestedCollection[A]]): IRDDNestedCollection[A] = {
    implicit val tag = weakTypeTag[IRDDNestedCollection[A]]
    proxyOps[IRDDNestedCollection[A]](p)(TagImplicits.typeTagToClassTag[IRDDNestedCollection[A]])
  }
  abstract class IRDDNestedCollectionElem[A, From, To <: IRDDNestedCollection[A]](iso: Iso[From, To])(implicit eA: Elem[A])
    extends ViewElem[From, To](iso) {
    override def convert(x: Rep[Reifiable[_]]) = convertIRDDNestedCollection(x.asRep[IRDDNestedCollection[A]])
    def convertIRDDNestedCollection(x : Rep[IRDDNestedCollection[A]]): Rep[To]
  }

  // elem for concrete class
  class RDDCollectionElem[A](iso: Iso[RDDCollectionData[A], RDDCollection[A]])(implicit val eA: Elem[A])
    extends IRDDCollectionElem[A, RDDCollectionData[A], RDDCollection[A]](iso) {
    def convertIRDDCollection(x: Rep[IRDDCollection[A]]) = RDDCollection(x.rdd)
  }

  // state representation type
  type RDDCollectionData[A] = SRDD[A]

  // 3) Iso for concrete class
  class RDDCollectionIso[A](implicit eA: Elem[A])
    extends Iso[RDDCollectionData[A], RDDCollection[A]] {
    override def from(p: Rep[RDDCollection[A]]) =
      unmkRDDCollection(p) match {
        case Some((rdd)) => rdd
        case None => !!!
      }
    override def to(p: Rep[SRDD[A]]) = {
      val rdd = p
      RDDCollection(rdd)
    }
    lazy val tag = {
      weakTypeTag[RDDCollection[A]]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[RDDCollection[A]]](RDDCollection(element[SRDD[A]].defaultRepValue))
    lazy val eTo = new RDDCollectionElem[A](this)
  }
  // 4) constructor and deconstructor
  abstract class RDDCollectionCompanionAbs extends CompanionBase[RDDCollectionCompanionAbs] with RDDCollectionCompanion {
    override def toString = "RDDCollection"

    def apply[A](rdd: RepRDD[A])(implicit eA: Elem[A]): Rep[RDDCollection[A]] =
      mkRDDCollection(rdd)
    def unapply[A:Elem](p: Rep[RDDCollection[A]]) = unmkRDDCollection(p)
  }
  def RDDCollection: Rep[RDDCollectionCompanionAbs]
  implicit def proxyRDDCollectionCompanion(p: Rep[RDDCollectionCompanionAbs]): RDDCollectionCompanionAbs = {
    proxyOps[RDDCollectionCompanionAbs](p)
  }

  class RDDCollectionCompanionElem extends CompanionElem[RDDCollectionCompanionAbs] {
    lazy val tag = weakTypeTag[RDDCollectionCompanionAbs]
    protected def getDefaultRep = RDDCollection
  }
  implicit lazy val RDDCollectionCompanionElem: RDDCollectionCompanionElem = new RDDCollectionCompanionElem

  implicit def proxyRDDCollection[A](p: Rep[RDDCollection[A]]): RDDCollection[A] =
    proxyOps[RDDCollection[A]](p)

  implicit class ExtendedRDDCollection[A](p: Rep[RDDCollection[A]])(implicit eA: Elem[A]) {
    def toData: Rep[RDDCollectionData[A]] = isoRDDCollection(eA).from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoRDDCollection[A](implicit eA: Elem[A]): Iso[RDDCollectionData[A], RDDCollection[A]] =
    new RDDCollectionIso[A]

  // 6) smart constructor and deconstructor
  def mkRDDCollection[A](rdd: RepRDD[A])(implicit eA: Elem[A]): Rep[RDDCollection[A]]
  def unmkRDDCollection[A:Elem](p: Rep[RDDCollection[A]]): Option[(Rep[SRDD[A]])]

  // elem for concrete class
  class PairRDDCollectionElem[A, B](iso: Iso[PairRDDCollectionData[A, B], PairRDDCollection[A, B]])(implicit val eA: Elem[A], val eB: Elem[B])
    extends IRDDPairCollectionElem[A, B, PairRDDCollectionData[A, B], PairRDDCollection[A, B]](iso) {
    def convertIRDDPairCollection(x: Rep[IRDDPairCollection[A, B]]) = PairRDDCollection(x.pairRDD)
  }

  // state representation type
  type PairRDDCollectionData[A, B] = SRDD[(A,B)]

  // 3) Iso for concrete class
  class PairRDDCollectionIso[A, B](implicit eA: Elem[A], eB: Elem[B])
    extends Iso[PairRDDCollectionData[A, B], PairRDDCollection[A, B]] {
    override def from(p: Rep[PairRDDCollection[A, B]]) =
      unmkPairRDDCollection(p) match {
        case Some((pairRDD)) => pairRDD
        case None => !!!
      }
    override def to(p: Rep[SRDD[(A,B)]]) = {
      val pairRDD = p
      PairRDDCollection(pairRDD)
    }
    lazy val tag = {
      weakTypeTag[PairRDDCollection[A, B]]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[PairRDDCollection[A, B]]](PairRDDCollection(element[SRDD[(A,B)]].defaultRepValue))
    lazy val eTo = new PairRDDCollectionElem[A, B](this)
  }
  // 4) constructor and deconstructor
  abstract class PairRDDCollectionCompanionAbs extends CompanionBase[PairRDDCollectionCompanionAbs] with PairRDDCollectionCompanion {
    override def toString = "PairRDDCollection"

    def apply[A, B](pairRDD: RepRDD[(A,B)])(implicit eA: Elem[A], eB: Elem[B]): Rep[PairRDDCollection[A, B]] =
      mkPairRDDCollection(pairRDD)
    def unapply[A:Elem, B:Elem](p: Rep[PairRDDCollection[A, B]]) = unmkPairRDDCollection(p)
  }
  def PairRDDCollection: Rep[PairRDDCollectionCompanionAbs]
  implicit def proxyPairRDDCollectionCompanion(p: Rep[PairRDDCollectionCompanionAbs]): PairRDDCollectionCompanionAbs = {
    proxyOps[PairRDDCollectionCompanionAbs](p)
  }

  class PairRDDCollectionCompanionElem extends CompanionElem[PairRDDCollectionCompanionAbs] {
    lazy val tag = weakTypeTag[PairRDDCollectionCompanionAbs]
    protected def getDefaultRep = PairRDDCollection
  }
  implicit lazy val PairRDDCollectionCompanionElem: PairRDDCollectionCompanionElem = new PairRDDCollectionCompanionElem

  implicit def proxyPairRDDCollection[A, B](p: Rep[PairRDDCollection[A, B]]): PairRDDCollection[A, B] =
    proxyOps[PairRDDCollection[A, B]](p)

  implicit class ExtendedPairRDDCollection[A, B](p: Rep[PairRDDCollection[A, B]])(implicit eA: Elem[A], eB: Elem[B]) {
    def toData: Rep[PairRDDCollectionData[A, B]] = isoPairRDDCollection(eA, eB).from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoPairRDDCollection[A, B](implicit eA: Elem[A], eB: Elem[B]): Iso[PairRDDCollectionData[A, B], PairRDDCollection[A, B]] =
    new PairRDDCollectionIso[A, B]

  // 6) smart constructor and deconstructor
  def mkPairRDDCollection[A, B](pairRDD: RepRDD[(A,B)])(implicit eA: Elem[A], eB: Elem[B]): Rep[PairRDDCollection[A, B]]
  def unmkPairRDDCollection[A:Elem, B:Elem](p: Rep[PairRDDCollection[A, B]]): Option[(Rep[SRDD[(A,B)]])]

  // elem for concrete class
  class RDDNestedCollectionElem[A](iso: Iso[RDDNestedCollectionData[A], RDDNestedCollection[A]])(implicit val eA: Elem[A])
    extends IRDDNestedCollectionElem[A, RDDNestedCollectionData[A], RDDNestedCollection[A]](iso) {
    def convertIRDDNestedCollection(x: Rep[IRDDNestedCollection[A]]) = RDDNestedCollection(x.values, x.segments)
  }

  // state representation type
  type RDDNestedCollectionData[A] = (IRDDCollection[A], PairRDDCollection[Int,Int])

  // 3) Iso for concrete class
  class RDDNestedCollectionIso[A](implicit eA: Elem[A])
    extends Iso[RDDNestedCollectionData[A], RDDNestedCollection[A]] {
    override def from(p: Rep[RDDNestedCollection[A]]) =
      unmkRDDNestedCollection(p) match {
        case Some((values, segments)) => Pair(values, segments)
        case None => !!!
      }
    override def to(p: Rep[(IRDDCollection[A], PairRDDCollection[Int,Int])]) = {
      val Pair(values, segments) = p
      RDDNestedCollection(values, segments)
    }
    lazy val tag = {
      weakTypeTag[RDDNestedCollection[A]]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[RDDNestedCollection[A]]](RDDNestedCollection(element[IRDDCollection[A]].defaultRepValue, element[PairRDDCollection[Int,Int]].defaultRepValue))
    lazy val eTo = new RDDNestedCollectionElem[A](this)
  }
  // 4) constructor and deconstructor
  abstract class RDDNestedCollectionCompanionAbs extends CompanionBase[RDDNestedCollectionCompanionAbs] with RDDNestedCollectionCompanion {
    override def toString = "RDDNestedCollection"
    def apply[A](p: Rep[RDDNestedCollectionData[A]])(implicit eA: Elem[A]): Rep[RDDNestedCollection[A]] =
      isoRDDNestedCollection(eA).to(p)
    def apply[A](values: Rep[IRDDCollection[A]], segments: Rep[PairRDDCollection[Int,Int]])(implicit eA: Elem[A]): Rep[RDDNestedCollection[A]] =
      mkRDDNestedCollection(values, segments)
    def unapply[A:Elem](p: Rep[RDDNestedCollection[A]]) = unmkRDDNestedCollection(p)
  }
  def RDDNestedCollection: Rep[RDDNestedCollectionCompanionAbs]
  implicit def proxyRDDNestedCollectionCompanion(p: Rep[RDDNestedCollectionCompanionAbs]): RDDNestedCollectionCompanionAbs = {
    proxyOps[RDDNestedCollectionCompanionAbs](p)
  }

  class RDDNestedCollectionCompanionElem extends CompanionElem[RDDNestedCollectionCompanionAbs] {
    lazy val tag = weakTypeTag[RDDNestedCollectionCompanionAbs]
    protected def getDefaultRep = RDDNestedCollection
  }
  implicit lazy val RDDNestedCollectionCompanionElem: RDDNestedCollectionCompanionElem = new RDDNestedCollectionCompanionElem

  implicit def proxyRDDNestedCollection[A](p: Rep[RDDNestedCollection[A]]): RDDNestedCollection[A] =
    proxyOps[RDDNestedCollection[A]](p)

  implicit class ExtendedRDDNestedCollection[A](p: Rep[RDDNestedCollection[A]])(implicit eA: Elem[A]) {
    def toData: Rep[RDDNestedCollectionData[A]] = isoRDDNestedCollection(eA).from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoRDDNestedCollection[A](implicit eA: Elem[A]): Iso[RDDNestedCollectionData[A], RDDNestedCollection[A]] =
    new RDDNestedCollectionIso[A]

  // 6) smart constructor and deconstructor
  def mkRDDNestedCollection[A](values: Rep[IRDDCollection[A]], segments: Rep[PairRDDCollection[Int,Int]])(implicit eA: Elem[A]): Rep[RDDNestedCollection[A]]
  def unmkRDDNestedCollection[A:Elem](p: Rep[RDDNestedCollection[A]]): Option[(Rep[IRDDCollection[A]], Rep[PairRDDCollection[Int,Int]])]
}

// Seq -----------------------------------
trait RDDCollectionsSeq extends RDDCollectionsDsl with SparkDslSeq {
  self: SparkDsl with RDDCollectionsDslSeq =>
  lazy val IRDDCollection: Rep[IRDDCollectionCompanionAbs] = new IRDDCollectionCompanionAbs with UserTypeSeq[IRDDCollectionCompanionAbs, IRDDCollectionCompanionAbs] {
    lazy val selfType = element[IRDDCollectionCompanionAbs]
  }

  case class SeqRDDCollection[A]
      (override val rdd: RepRDD[A])
      (implicit eA: Elem[A])
    extends RDDCollection[A](rdd)
        with UserTypeSeq[IRDDCollection[A], RDDCollection[A]] {
    lazy val selfType = element[RDDCollection[A]].asInstanceOf[Elem[IRDDCollection[A]]]
  }
  lazy val RDDCollection = new RDDCollectionCompanionAbs with UserTypeSeq[RDDCollectionCompanionAbs, RDDCollectionCompanionAbs] {
    lazy val selfType = element[RDDCollectionCompanionAbs]
  }

  def mkRDDCollection[A]
      (rdd: RepRDD[A])(implicit eA: Elem[A]): Rep[RDDCollection[A]] =
      new SeqRDDCollection[A](rdd)
  def unmkRDDCollection[A:Elem](p: Rep[RDDCollection[A]]) =
    Some((p.rdd))

  case class SeqPairRDDCollection[A, B]
      (override val pairRDD: RepRDD[(A,B)])
      (implicit eA: Elem[A], eB: Elem[B])
    extends PairRDDCollection[A, B](pairRDD)
        with UserTypeSeq[IRDDPairCollection[A,B], PairRDDCollection[A, B]] {
    lazy val selfType = element[PairRDDCollection[A, B]].asInstanceOf[Elem[IRDDPairCollection[A,B]]]
  }
  lazy val PairRDDCollection = new PairRDDCollectionCompanionAbs with UserTypeSeq[PairRDDCollectionCompanionAbs, PairRDDCollectionCompanionAbs] {
    lazy val selfType = element[PairRDDCollectionCompanionAbs]
  }

  def mkPairRDDCollection[A, B]
      (pairRDD: RepRDD[(A,B)])(implicit eA: Elem[A], eB: Elem[B]): Rep[PairRDDCollection[A, B]] =
      new SeqPairRDDCollection[A, B](pairRDD)
  def unmkPairRDDCollection[A:Elem, B:Elem](p: Rep[PairRDDCollection[A, B]]) =
    Some((p.pairRDD))

  case class SeqRDDNestedCollection[A]
      (override val values: Rep[IRDDCollection[A]], override val segments: Rep[PairRDDCollection[Int,Int]])
      (implicit eA: Elem[A])
    extends RDDNestedCollection[A](values, segments)
        with UserTypeSeq[IRDDNestedCollection[A], RDDNestedCollection[A]] {
    lazy val selfType = element[RDDNestedCollection[A]].asInstanceOf[Elem[IRDDNestedCollection[A]]]
  }
  lazy val RDDNestedCollection = new RDDNestedCollectionCompanionAbs with UserTypeSeq[RDDNestedCollectionCompanionAbs, RDDNestedCollectionCompanionAbs] {
    lazy val selfType = element[RDDNestedCollectionCompanionAbs]
  }

  def mkRDDNestedCollection[A]
      (values: Rep[IRDDCollection[A]], segments: Rep[PairRDDCollection[Int,Int]])(implicit eA: Elem[A]): Rep[RDDNestedCollection[A]] =
      new SeqRDDNestedCollection[A](values, segments)
  def unmkRDDNestedCollection[A:Elem](p: Rep[RDDNestedCollection[A]]) =
    Some((p.values, p.segments))
}

// Exp -----------------------------------
trait RDDCollectionsExp extends RDDCollectionsDsl with SparkDslExp {
  self: SparkDsl with RDDCollectionsDslExp =>
  lazy val IRDDCollection: Rep[IRDDCollectionCompanionAbs] = new IRDDCollectionCompanionAbs with UserTypeDef[IRDDCollectionCompanionAbs, IRDDCollectionCompanionAbs] {
    lazy val selfType = element[IRDDCollectionCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  case class ExpRDDCollection[A]
      (override val rdd: RepRDD[A])
      (implicit eA: Elem[A])
    extends RDDCollection[A](rdd) with UserTypeDef[IRDDCollection[A], RDDCollection[A]] {
    lazy val selfType = element[RDDCollection[A]].asInstanceOf[Elem[IRDDCollection[A]]]
    override def mirror(t: Transformer) = ExpRDDCollection[A](t(rdd))
  }

  lazy val RDDCollection: Rep[RDDCollectionCompanionAbs] = new RDDCollectionCompanionAbs with UserTypeDef[RDDCollectionCompanionAbs, RDDCollectionCompanionAbs] {
    lazy val selfType = element[RDDCollectionCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object RDDCollectionMethods {
    object arr {
      def unapply(d: Def[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "arr" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object lst {
      def unapply(d: Def[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "lst" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply {
      def unapply(d: Def[_]): Option[(Rep[RDDCollection[A]], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(i, _*), _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "apply"&& method.getAnnotation(classOf[scalan.OverloadId]) == null =>
          Some((receiver, i)).asInstanceOf[Option[(Rep[RDDCollection[A]], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDCollection[A]], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object length {
      def unapply(d: Def[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "length" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object slice {
      def unapply(d: Def[_]): Option[(Rep[RDDCollection[A]], Rep[Int], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(offset, length, _*), _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "slice" =>
          Some((receiver, offset, length)).asInstanceOf[Option[(Rep[RDDCollection[A]], Rep[Int], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDCollection[A]], Rep[Int], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply_many {
      def unapply(d: Def[_]): Option[(Rep[RDDCollection[A]], Coll[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(indices, _*), _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "apply" && { val ann = method.getAnnotation(classOf[scalan.OverloadId]); ann != null && ann.value == "many" } =>
          Some((receiver, indices)).asInstanceOf[Option[(Rep[RDDCollection[A]], Coll[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDCollection[A]], Coll[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `map`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `mapBy`: Method's return type Coll[B] is not a Rep

    object reduce {
      def unapply(d: Def[_]): Option[(Rep[RDDCollection[A]], RepMonoid[A]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(m, _*), _) if receiver.elem.isInstanceOf[RDDCollectionElem[_]] && method.getName == "reduce" =>
          Some((receiver, m)).asInstanceOf[Option[(Rep[RDDCollection[A]], RepMonoid[A]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDCollection[A]], RepMonoid[A]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `zip`: Method's return type Coll[(A,B)] is not a Rep

    // WARNING: Cannot generate matcher for method `update`: Method's return type Coll[A] is not a Rep

    // WARNING: Cannot generate matcher for method `updateMany`: Method's return type Coll[A] is not a Rep

    // WARNING: Cannot generate matcher for method `filter`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `flatMap`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `append`: Method's return type Coll[A] is not a Rep
  }

  object RDDCollectionCompanionMethods {
    object defaultOf {
      def unapply(d: Def[_]): Option[Elem[A] forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(ea, _*), _) if receiver.elem.isInstanceOf[RDDCollectionCompanionElem] && method.getName == "defaultOf" =>
          Some(ea).asInstanceOf[Option[Elem[A] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Elem[A] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  def mkRDDCollection[A]
    (rdd: RepRDD[A])(implicit eA: Elem[A]): Rep[RDDCollection[A]] =
    new ExpRDDCollection[A](rdd)
  def unmkRDDCollection[A:Elem](p: Rep[RDDCollection[A]]) =
    Some((p.rdd))

  case class ExpPairRDDCollection[A, B]
      (override val pairRDD: RepRDD[(A,B)])
      (implicit eA: Elem[A], eB: Elem[B])
    extends PairRDDCollection[A, B](pairRDD) with UserTypeDef[IRDDPairCollection[A,B], PairRDDCollection[A, B]] {
    lazy val selfType = element[PairRDDCollection[A, B]].asInstanceOf[Elem[IRDDPairCollection[A,B]]]
    override def mirror(t: Transformer) = ExpPairRDDCollection[A, B](t(pairRDD))
  }

  lazy val PairRDDCollection: Rep[PairRDDCollectionCompanionAbs] = new PairRDDCollectionCompanionAbs with UserTypeDef[PairRDDCollectionCompanionAbs, PairRDDCollectionCompanionAbs] {
    lazy val selfType = element[PairRDDCollectionCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object PairRDDCollectionMethods {
    object as {
      def unapply(d: Def[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "as" =>
          Some(receiver).asInstanceOf[Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object bs {
      def unapply(d: Def[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "bs" =>
          Some(receiver).asInstanceOf[Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object arr {
      def unapply(d: Def[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "arr" =>
          Some(receiver).asInstanceOf[Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object lst {
      def unapply(d: Def[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "lst" =>
          Some(receiver).asInstanceOf[Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply {
      def unapply(d: Def[_]): Option[(Rep[PairRDDCollection[A, B]], Rep[Int]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(i, _*), _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "apply"&& method.getAnnotation(classOf[scalan.OverloadId]) == null =>
          Some((receiver, i)).asInstanceOf[Option[(Rep[PairRDDCollection[A, B]], Rep[Int]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[PairRDDCollection[A, B]], Rep[Int]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object length {
      def unapply(d: Def[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "length" =>
          Some(receiver).asInstanceOf[Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[PairRDDCollection[A, B]] forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object slice {
      def unapply(d: Def[_]): Option[(Rep[PairRDDCollection[A, B]], Rep[Int], Rep[Int]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(offset, length, _*), _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "slice" =>
          Some((receiver, offset, length)).asInstanceOf[Option[(Rep[PairRDDCollection[A, B]], Rep[Int], Rep[Int]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[PairRDDCollection[A, B]], Rep[Int], Rep[Int]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `apply`: Method's return type Coll[(A,B)] is not a Rep

    // WARNING: Cannot generate matcher for method `map`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `mapBy`: Method's return type Coll[C] is not a Rep

    object reduce {
      def unapply(d: Def[_]): Option[(Rep[PairRDDCollection[A, B]], RepMonoid[(A,B)]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(m, _*), _) if receiver.elem.isInstanceOf[PairRDDCollectionElem[_, _]] && method.getName == "reduce" =>
          Some((receiver, m)).asInstanceOf[Option[(Rep[PairRDDCollection[A, B]], RepMonoid[(A,B)]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[PairRDDCollection[A, B]], RepMonoid[(A,B)]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `zip`: Method's return type Coll[((A,B),C)] is not a Rep

    // WARNING: Cannot generate matcher for method `update`: Method's return type Coll[(A,B)] is not a Rep

    // WARNING: Cannot generate matcher for method `updateMany`: Method's return type Coll[(A,B)] is not a Rep

    // WARNING: Cannot generate matcher for method `filter`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `flatMap`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `append`: Method's return type Coll[(A,B)] is not a Rep
  }

  object PairRDDCollectionCompanionMethods {
    object defaultOf {
      def unapply(d: Def[_]): Option[(Elem[A], Elem[B]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(ea, eb, _*), _) if receiver.elem.isInstanceOf[PairRDDCollectionCompanionElem] && method.getName == "defaultOf" =>
          Some((ea, eb)).asInstanceOf[Option[(Elem[A], Elem[B]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Elem[A], Elem[B]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  def mkPairRDDCollection[A, B]
    (pairRDD: RepRDD[(A,B)])(implicit eA: Elem[A], eB: Elem[B]): Rep[PairRDDCollection[A, B]] =
    new ExpPairRDDCollection[A, B](pairRDD)
  def unmkPairRDDCollection[A:Elem, B:Elem](p: Rep[PairRDDCollection[A, B]]) =
    Some((p.pairRDD))

  case class ExpRDDNestedCollection[A]
      (override val values: Rep[IRDDCollection[A]], override val segments: Rep[PairRDDCollection[Int,Int]])
      (implicit eA: Elem[A])
    extends RDDNestedCollection[A](values, segments) with UserTypeDef[IRDDNestedCollection[A], RDDNestedCollection[A]] {
    lazy val selfType = element[RDDNestedCollection[A]].asInstanceOf[Elem[IRDDNestedCollection[A]]]
    override def mirror(t: Transformer) = ExpRDDNestedCollection[A](t(values), t(segments))
  }

  lazy val RDDNestedCollection: Rep[RDDNestedCollectionCompanionAbs] = new RDDNestedCollectionCompanionAbs with UserTypeDef[RDDNestedCollectionCompanionAbs, RDDNestedCollectionCompanionAbs] {
    lazy val selfType = element[RDDNestedCollectionCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object RDDNestedCollectionMethods {
    object segOffsets {
      def unapply(d: Def[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "segOffsets" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDNestedCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object segLens {
      def unapply(d: Def[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "segLens" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDNestedCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object length {
      def unapply(d: Def[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "length" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDNestedCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(i, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "apply"&& method.getAnnotation(classOf[scalan.OverloadId]) == null =>
          Some((receiver, i)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object arr {
      def unapply(d: Def[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "arr" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDNestedCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object lst {
      def unapply(d: Def[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "lst" =>
          Some(receiver).asInstanceOf[Option[Rep[RDDNestedCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[RDDNestedCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object slice {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(offset, length, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "slice" =>
          Some((receiver, offset, length)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply_many {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Coll[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(indices, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "apply" && { val ann = method.getAnnotation(classOf[scalan.OverloadId]); ann != null && ann.value == "many" } =>
          Some((receiver, indices)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Coll[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Coll[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `map`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `mapBy`: Method's return type Coll[B] is not a Rep

    // WARNING: Cannot generate matcher for method `reduce`: Method's return type Coll[A] is not a Rep

    // WARNING: Cannot generate matcher for method `zip`: Method's return type Coll[(Collection[A],B)] is not a Rep

    object update {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Collection[A]]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(idx, value, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "update" =>
          Some((receiver, idx, value)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Collection[A]]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Int], Rep[Collection[A]]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object updateMany {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Coll[Int], Coll[Collection[A]]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(idxs, vals, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "updateMany" =>
          Some((receiver, idxs, vals)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Coll[Int], Coll[Collection[A]]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Coll[Int], Coll[Collection[A]]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `filter`: Method has function arguments f

    // WARNING: Cannot generate matcher for method `flatMap`: Method has function arguments f

    object append {
      def unapply(d: Def[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Collection[A]]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(value, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionElem[_]] && method.getName == "append" =>
          Some((receiver, value)).asInstanceOf[Option[(Rep[RDDNestedCollection[A]], Rep[Collection[A]]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDNestedCollection[A]], Rep[Collection[A]]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object RDDNestedCollectionCompanionMethods {
    object defaultOf {
      def unapply(d: Def[_]): Option[Elem[A] forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(ea, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionCompanionElem] && method.getName == "defaultOf" =>
          Some(ea).asInstanceOf[Option[Elem[A] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Elem[A] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object createRDDNestedCollection {
      def unapply(d: Def[_]): Option[(Rep[RDDCollection[A]], Rep[PairRDDCollection[Int,Int]]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(vals, segments, _*), _) if receiver.elem.isInstanceOf[RDDNestedCollectionCompanionElem] && method.getName == "createRDDNestedCollection" =>
          Some((vals, segments)).asInstanceOf[Option[(Rep[RDDCollection[A]], Rep[PairRDDCollection[Int,Int]]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[RDDCollection[A]], Rep[PairRDDCollection[Int,Int]]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  def mkRDDNestedCollection[A]
    (values: Rep[IRDDCollection[A]], segments: Rep[PairRDDCollection[Int,Int]])(implicit eA: Elem[A]): Rep[RDDNestedCollection[A]] =
    new ExpRDDNestedCollection[A](values, segments)
  def unmkRDDNestedCollection[A:Elem](p: Rep[RDDNestedCollection[A]]) =
    Some((p.values, p.segments))

  object IRDDCollectionMethods {
    object rdd {
      def unapply(d: Def[_]): Option[Rep[IRDDCollection[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[IRDDCollectionElem[_, _, _]] && method.getName == "rdd" =>
          Some(receiver).asInstanceOf[Option[Rep[IRDDCollection[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[IRDDCollection[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object IRDDCollectionCompanionMethods {
    // WARNING: Cannot generate matcher for method `defaultOf`: Method's return type Default[Rep[IRDDCollection[A]]] is not a Rep
  }
}