package com.scalan.spark.method

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.{PairRDDFunctions, RDD}

import scala.reflect.ClassTag

object Methods {

  def reduceByKey[K : ClassTag, V : ClassTag](pRDDF: PairRDDFunctions[K, V], func: ((V, V)) => V) : PairRDDFunctions[K, V] =
    new PairRDDFunctions[K, V](pRDDF.reduceByKey((p1, p2) => func((p1, p2))))

  def fold[T](obj: RDD[T], zeroValue: T, op: ((T, T)) => T): T = obj.fold(zeroValue)((p1, p2) => op((p1, p2)))

  def sparkContext(conf: SparkConf) = new SparkContext(conf)

  def fromList[A](list: List[A]): Seq[A] = Seq(list: _*)

  def newSeq[A](ar: Array[A]): Seq[A] = Seq(ar: _*)

  def combineByKey[K,V](prdd: RDD[(K,V)]): RDD[(K,Array[V])] = {
    ???
  }

  def countByKey[K,V](prdd: RDD[(K,V)]): Map[K,Int]  = prdd.countByKey

  def foldByKey[K,V](prdd: RDD[(K,V)])(zeroValue: V)(op: (V, V) => V): RDD[(K,V)] = prdd.foldByKey(zeroValue)(op)
}