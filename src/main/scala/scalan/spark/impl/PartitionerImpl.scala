package scalan.spark
package impl

import scalan._
import scalan.common.Default
import org.apache.spark.{HashPartitioner, Partitioner}
import scala.reflect.runtime.universe._
import scalan.common.Default

// Abs -----------------------------------
trait PartitionersAbs extends ScalanCommunityDsl with Partitioners {
  self: SparkDsl =>
  // single proxy for each type family
  implicit def proxySPartitioner(p: Rep[SPartitioner]): SPartitioner = {
    implicit val tag = weakTypeTag[SPartitioner]
    proxyOps[SPartitioner](p)(TagImplicits.typeTagToClassTag[SPartitioner])
  }

  abstract class SPartitionerElem[From, To <: SPartitioner](iso: Iso[From, To])
    extends ViewElem[From, To](iso) {
    override def convert(x: Rep[Reifiable[_]]) = convertSPartitioner(x.asRep[SPartitioner])
    def convertSPartitioner(x : Rep[SPartitioner]): Rep[To]
  }
  trait SPartitionerCompanionElem extends CompanionElem[SPartitionerCompanionAbs]
  implicit lazy val SPartitionerCompanionElem: SPartitionerCompanionElem = new SPartitionerCompanionElem {
    lazy val tag = weakTypeTag[SPartitionerCompanionAbs]
    protected def getDefaultRep = SPartitioner
  }

  abstract class SPartitionerCompanionAbs extends CompanionBase[SPartitionerCompanionAbs] with SPartitionerCompanion {
    override def toString = "SPartitioner"
  }
  def SPartitioner: Rep[SPartitionerCompanionAbs]
  implicit def proxySPartitionerCompanion(p: Rep[SPartitionerCompanion]): SPartitionerCompanion = {
    proxyOps[SPartitionerCompanion](p)
  }

  // single proxy for each type family
  implicit def proxySBasePartitioner(p: Rep[SBasePartitioner]): SBasePartitioner = {
    implicit val tag = weakTypeTag[SBasePartitioner]
    proxyOps[SBasePartitioner](p)(TagImplicits.typeTagToClassTag[SBasePartitioner])
  }
  abstract class SBasePartitionerElem[From, To <: SBasePartitioner](iso: Iso[From, To])
    extends ViewElem[From, To](iso) {
    override def convert(x: Rep[Reifiable[_]]) = convertSBasePartitioner(x.asRep[SBasePartitioner])
    def convertSBasePartitioner(x : Rep[SBasePartitioner]): Rep[To]
  }
}

// Seq -----------------------------------
trait PartitionersSeq extends PartitionersDsl with ScalanCommunityDslSeq {
  self: SparkDslSeq =>
  lazy val SPartitioner: Rep[SPartitionerCompanionAbs] = new SPartitionerCompanionAbs with UserTypeSeq[SPartitionerCompanionAbs, SPartitionerCompanionAbs] {
    lazy val selfType = element[SPartitionerCompanionAbs]
  }
}

// Exp -----------------------------------
trait PartitionersExp extends PartitionersDsl with ScalanCommunityDslExp {
  self: SparkDslExp =>
  lazy val SPartitioner: Rep[SPartitionerCompanionAbs] = new SPartitionerCompanionAbs with UserTypeDef[SPartitionerCompanionAbs, SPartitionerCompanionAbs] {
    lazy val selfType = element[SPartitionerCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object SPartitionerMethods {
    object wrappedValueOfBaseType {
      def unapply(d: Def[_]): Option[Rep[SPartitioner]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[SPartitionerElem[_, _]] && method.getName == "wrappedValueOfBaseType" =>
          Some(receiver).asInstanceOf[Option[Rep[SPartitioner]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[SPartitioner]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object SPartitionerCompanionMethods {
  }
}
