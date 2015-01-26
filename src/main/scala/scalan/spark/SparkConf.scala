package scalan.spark

import scalan._
import org.apache.spark.SparkConf

trait SparkConfs extends Base with BaseTypes { self: SparkConfsDsl =>
  type RepSparkConf = Rep[SparkConf]

  /** Configuration for a Spark application.
    * SparkConf allows to set various Spark parameters as key-value pairs.*/
  trait SSparkConf extends BaseTypeEx[SparkConf, SSparkConf] {self =>
    /** Sets a name for the application. */
    @External def setAppName(name: Rep[String]): RepSparkConf

    /** URL of the master. For example, local, local[8] or spark://master:7077 */
    @External def setMaster(master: Rep[String]): RepSparkConf

    /** Set a configuration variable. */
    @External def set(key: Rep[String], value: Rep[String]): RepSparkConf
  }
}

trait SparkConfsDsl extends impl.SparkConfsAbs
trait SparkConfsDslSeq extends impl.SparkConfsSeq
trait SparkConfsDslExp extends impl.SparkConfsExp
