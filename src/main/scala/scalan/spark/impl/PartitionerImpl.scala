package scalan.spark
package impl

import scalan._
import scalan.common.Default
import org.apache.spark.{HashPartitioner, Partitioner}
import scala.reflect.runtime.universe._
import scalan.common.Default

// Abs -----------------------------------
trait PartitionersAbs extends Scalan with Partitioners
{ self: SparkDsl =>
  // single proxy for each type family
  implicit def proxySPartitioner(p: Rep[SPartitioner]): SPartitioner =
    proxyOps[SPartitioner](p)
  // BaseTypeEx proxy
  implicit def proxyPartitioner(p: Rep[Partitioner]): SPartitioner =
    proxyOps[SPartitioner](p.asRep[SPartitioner])

  implicit def defaultSPartitionerElem: Elem[SPartitioner] = element[SPartitionerImpl].asElem[SPartitioner]
  implicit def PartitionerElement: Elem[Partitioner]

  abstract class SPartitionerElem[From, To <: SPartitioner](iso: Iso[From, To]) extends ViewElem[From, To]()(iso)

  trait SPartitionerCompanionElem extends CompanionElem[SPartitionerCompanionAbs]
  implicit lazy val SPartitionerCompanionElem: SPartitionerCompanionElem = new SPartitionerCompanionElem {
    lazy val tag = typeTag[SPartitionerCompanionAbs]
    protected def getDefaultRep = SPartitioner
  }

  abstract class SPartitionerCompanionAbs extends CompanionBase[SPartitionerCompanionAbs] with SPartitionerCompanion {
    override def toString = "SPartitioner"
    
  }
  def SPartitioner: Rep[SPartitionerCompanionAbs]
  implicit def proxySPartitionerCompanion(p: Rep[SPartitionerCompanion]): SPartitionerCompanion = {
    proxyOps[SPartitionerCompanion](p)
  }

  //default wrapper implementation
    abstract class SPartitionerImpl(val wrappedValueOfBaseType: Rep[Partitioner]) extends SPartitioner {
    
  }
  trait SPartitionerImplCompanion
  // elem for concrete class
  class SPartitionerImplElem(iso: Iso[SPartitionerImplData, SPartitionerImpl]) extends SPartitionerElem[SPartitionerImplData, SPartitionerImpl](iso)

  // state representation type
  type SPartitionerImplData = Partitioner

  // 3) Iso for concrete class
  class SPartitionerImplIso
    extends Iso[SPartitionerImplData, SPartitionerImpl] {
    override def from(p: Rep[SPartitionerImpl]) =
      unmkSPartitionerImpl(p) match {
        case Some((wrappedValueOfBaseType)) => wrappedValueOfBaseType
        case None => !!!
      }
    override def to(p: Rep[Partitioner]) = {
      val wrappedValueOfBaseType = p
      SPartitionerImpl(wrappedValueOfBaseType)
    }
    lazy val tag = {
      weakTypeTag[SPartitionerImpl]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[SPartitionerImpl]](SPartitionerImpl(Default.defaultOf[Partitioner]))
    lazy val eTo = new SPartitionerImplElem(this)
  }
  // 4) constructor and deconstructor
  abstract class SPartitionerImplCompanionAbs extends CompanionBase[SPartitionerImplCompanionAbs] with SPartitionerImplCompanion {
    override def toString = "SPartitionerImpl"

    def apply(wrappedValueOfBaseType: Rep[Partitioner]): Rep[SPartitionerImpl] =
      mkSPartitionerImpl(wrappedValueOfBaseType)
    def unapply(p: Rep[SPartitionerImpl]) = unmkSPartitionerImpl(p)
  }
  def SPartitionerImpl: Rep[SPartitionerImplCompanionAbs]
  implicit def proxySPartitionerImplCompanion(p: Rep[SPartitionerImplCompanionAbs]): SPartitionerImplCompanionAbs = {
    proxyOps[SPartitionerImplCompanionAbs](p)
  }

  class SPartitionerImplCompanionElem extends CompanionElem[SPartitionerImplCompanionAbs] {
    lazy val tag = typeTag[SPartitionerImplCompanionAbs]
    protected def getDefaultRep = SPartitionerImpl
  }
  implicit lazy val SPartitionerImplCompanionElem: SPartitionerImplCompanionElem = new SPartitionerImplCompanionElem

  implicit def proxySPartitionerImpl(p: Rep[SPartitionerImpl]): SPartitionerImpl =
    proxyOps[SPartitionerImpl](p)

  implicit class ExtendedSPartitionerImpl(p: Rep[SPartitionerImpl]) {
    def toData: Rep[SPartitionerImplData] = isoSPartitionerImpl.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoSPartitionerImpl: Iso[SPartitionerImplData, SPartitionerImpl] =
    new SPartitionerImplIso

  // 6) smart constructor and deconstructor
  def mkSPartitionerImpl(wrappedValueOfBaseType: Rep[Partitioner]): Rep[SPartitionerImpl]
  def unmkSPartitionerImpl(p: Rep[SPartitionerImpl]): Option[(Rep[Partitioner])]
}

// Seq -----------------------------------
trait PartitionersSeq extends PartitionersAbs with PartitionersDsl with ScalanSeq
{ self: SparkDslSeq =>
  lazy val SPartitioner: Rep[SPartitionerCompanionAbs] = new SPartitionerCompanionAbs with UserTypeSeq[SPartitionerCompanionAbs, SPartitionerCompanionAbs] {
    lazy val selfType = element[SPartitionerCompanionAbs]
    
  }

    // override proxy if we deal with BaseTypeEx
  override def proxyPartitioner(p: Rep[Partitioner]): SPartitioner =
    proxyOpsEx[Partitioner,SPartitioner, SeqSPartitionerImpl](p, bt => SeqSPartitionerImpl(bt))

    implicit lazy val PartitionerElement: Elem[Partitioner] = new SeqBaseElemEx[Partitioner, SPartitioner](element[SPartitioner])

  case class SeqSPartitionerImpl
      (override val wrappedValueOfBaseType: Rep[Partitioner])
      
    extends SPartitionerImpl(wrappedValueOfBaseType)
        with UserTypeSeq[SPartitioner, SPartitionerImpl] {
    lazy val selfType = element[SPartitionerImpl].asInstanceOf[Elem[SPartitioner]]
    
  }
  lazy val SPartitionerImpl = new SPartitionerImplCompanionAbs with UserTypeSeq[SPartitionerImplCompanionAbs, SPartitionerImplCompanionAbs] {
    lazy val selfType = element[SPartitionerImplCompanionAbs]
  }

  def mkSPartitionerImpl
      (wrappedValueOfBaseType: Rep[Partitioner]) =
      new SeqSPartitionerImpl(wrappedValueOfBaseType)
  def unmkSPartitionerImpl(p: Rep[SPartitionerImpl]) =
    Some((p.wrappedValueOfBaseType))
}

// Exp -----------------------------------
trait PartitionersExp extends PartitionersAbs with PartitionersDsl with ScalanExp
{ self: SparkDslExp =>
  lazy val SPartitioner: Rep[SPartitionerCompanionAbs] = new SPartitionerCompanionAbs with UserTypeDef[SPartitionerCompanionAbs, SPartitionerCompanionAbs] {
    lazy val selfType = element[SPartitionerCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  implicit lazy val PartitionerElement: Elem[Partitioner] = new ExpBaseElemEx[Partitioner, SPartitioner](element[SPartitioner])

  case class ExpSPartitionerImpl
      (override val wrappedValueOfBaseType: Rep[Partitioner])
      
    extends SPartitionerImpl(wrappedValueOfBaseType) with UserTypeDef[SPartitioner, SPartitionerImpl] {
    lazy val selfType = element[SPartitionerImpl].asInstanceOf[Elem[SPartitioner]]
    override def mirror(t: Transformer) = ExpSPartitionerImpl(t(wrappedValueOfBaseType))
  }

  lazy val SPartitionerImpl: Rep[SPartitionerImplCompanionAbs] = new SPartitionerImplCompanionAbs with UserTypeDef[SPartitionerImplCompanionAbs, SPartitionerImplCompanionAbs] {
    lazy val selfType = element[SPartitionerImplCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object SPartitionerImplMethods {

  }



  def mkSPartitionerImpl
    (wrappedValueOfBaseType: Rep[Partitioner]) =
    new ExpSPartitionerImpl(wrappedValueOfBaseType)
  def unmkSPartitionerImpl(p: Rep[SPartitionerImpl]) =
    Some((p.wrappedValueOfBaseType))

  object SPartitionerMethods {

  }

  object SPartitionerCompanionMethods {

  }
}