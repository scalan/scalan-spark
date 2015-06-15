package com.scalan.spark.method

import org.apache.spark.{SparkException, SparkContext, SparkConf}
import org.apache.spark.rdd.{PairRDDFunctions, RDD}

import scala.reflect.ClassTag
import scala.collection.JavaConversions._

object Methods {

  def reduceByKey[K : ClassTag, V : ClassTag](pRDDF: PairRDDFunctions[K, V], func: ((V, V)) => V) : PairRDDFunctions[K, V] =
    new PairRDDFunctions[K, V](pRDDF.reduceByKey((p1, p2) => func((p1, p2))))

  def fold[T](obj: RDD[T], zeroValue: T, op: ((T, T)) => T): T = obj.fold(zeroValue)((p1, p2) => op((p1, p2)))

  def sparkContext(conf: SparkConf) = new SparkContext(conf)

  def fromList[A](list: List[A]): Seq[A] = Seq(list: _*)

  def newSeq[A](ar: Array[A]): Seq[A] = Seq(ar: _*)

  def seqMap[A,B](seq: Seq[A], f: A => B) = seq.map[B,Seq[B]](f)

  def groupByKey[K,V](prdd: org.apache.spark.rdd.PairRDDFunctions[K,V]): RDD[(K,Seq[V])] =   prdd.groupByKey().map({in => (in._1, in._2.toSeq)})

  def countByKey[K,V](prdd: org.apache.spark.rdd.PairRDDFunctions[K,V]): java.util.HashMap[K,Long]  = new java.util.HashMap(prdd.countByKey)

  def foldByKey[K,V](prdd: org.apache.spark.rdd.PairRDDFunctions[K,V], zeroValue: V, op: ((V, V)) => V): org.apache.spark.rdd.RDD[(K,V)]  = prdd.foldByKey(zeroValue)((p1, p2) => op((p1,p2)))

  def groupWithExt[K:Ordering:ClassTag,V, W](prdd1: org.apache.spark.rdd.PairRDDFunctions[K,V], prdd2: RDD[(K,W)]) : RDD[(K, (Seq[V], Seq[W]))] = {
    val groupped = prdd1.groupWith(prdd2)
    val sorted = (new org.apache.spark.rdd.OrderedRDDFunctions[K, (Iterable[V], Iterable[W]), (K, (Iterable[V], Iterable[W]))](groupped)).repartitionAndSortWithinPartitions(
      defaultPartitioner(prdd2.context.defaultParallelism))
    sorted.map({ in => (in._1, (in._2._1.toSeq, in._2._2.toSeq))})
  }
  def zipSafe[A:ClassTag, B:ClassTag](rdd1: RDD[A], rdd2: RDD[B]): RDD[(A,B)] = {
    val irdd1 = rdd1.zipWithIndex.map { case (v,i) => i->v}
    val irdd2 = rdd2.zipWithIndex.map { case (v,i) => i->v}
    val joined = (new PairRDDFunctions(irdd1)).leftOuterJoin(irdd2)
    joined.map { case (i, (v1, v2Opt)) => (v1, v2Opt.getOrElse(throw new SparkException ("Zipped RDDs should have same length!"))) }
  }
  def repartition[A : ClassTag](rdd: RDD[A]) = {
    val withIdxs: PairRDDFunctions[Long,A] = new PairRDDFunctions[Long, A] (rdd.zipWithIndex.map { case(v,k) => (k,v)})
    withIdxs.partitionBy(new org.apache.spark.HashPartitioner(rdd.context.defaultParallelism)).map {_._2}
  }

  def defaultPartitioner(numSlices: Int) = new org.apache.spark.HashPartitioner(numSlices)

  def partitionBy[A: ClassTag](rdd: RDD[A], partitioner: org.apache.spark.Partitioner) = {
    val withIdxs: PairRDDFunctions[Long,A] = new PairRDDFunctions[Long, A] (rdd.zipWithIndex.map { case(v,k) => (k,v)})
    withIdxs.partitionBy(partitioner).map {_._2}
  }

  def partLens[A](rdd: RDD[A]): Array[Int] = { rdd.mapPartitions(iter => Array(iter.size).iterator, true)}.collect
  def printZipParts0[A,B](rdd1: RDD[A], rdd2:RDD[B], message: String) = println(message + " : first - " + partLens(rdd1).mkString(",") + " , second - " + partLens(rdd2).mkString(","))
  def printZipParts[A,B](rdd1: RDD[A], rdd2:RDD[B], res: RDD[(A,B)], message: String) = {
    try {
      println(message + " : first - " + partLens(rdd1).mkString(",") + " , second - " + partLens(rdd2).mkString(",") + " , result - " + partLens(res).mkString(" ,"))
    } catch {
      case _ => printZipParts0(rdd1, rdd2, message + " - Error when zipping rdds: ")
    }
  }
}