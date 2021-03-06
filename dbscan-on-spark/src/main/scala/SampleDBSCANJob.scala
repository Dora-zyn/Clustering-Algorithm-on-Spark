/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package com.irvingc.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.slf4j.LoggerFactory
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.dbscan.DBSCAN

object SampleDBSCANJob {

  val log = LoggerFactory.getLogger(SampleDBSCANJob.getClass)

  def main(args: Array[String]) {
//
//    if (args.length < 3) {
//      System.err.println("You must pass the arguments: <src file> <dest file> <parallelism>")
//      System.exit(1)
//    }

//    val (src, dest, maxPointsPerPartition, eps, minPoints) =
//      (args(0), args(1), args(2).toInt, args(3).toFloat, args(4).toInt)

    val src = "F:\\intellij\\dbscan-on-spark-master\\input\\data"
    val dest = ""
    val maxPointsPerPartition=1000
    val eps = 10
    val minPoints = 20


    val destOut = dest.split('/').last

    val conf = new SparkConf().setAppName(s"DBSCAN(eps=$eps, min=$minPoints, max=$maxPointsPerPartition) -> $destOut").setMaster("local")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    // conf.set("spark.storage.memoryFraction", "0.1")
    val sc = new SparkContext(conf)

    val data = sc.textFile(src)

    val parsedData = data.map(s => Vectors.dense(s.split(',').map(_.toDouble))).cache()

    log.info(s"EPS: $eps minPoints: $minPoints")

    val model = DBSCAN.train(
      parsedData,
      eps = eps,
      minPoints = minPoints,
      maxPointsPerPartition = maxPointsPerPartition)

    model.labeledPoints.map(p => s"${p.x},${p.y},${p.cluster}")
      .foreach(println(_))
    //      .saveAsTextFile(dest)
    log.info("Stopping Spark Context...")
    sc.stop()

  }
}