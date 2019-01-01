package com.hochgi

import akka.util.ByteString

package object util {

  val endln = ByteString("\n")

  def incrementByKey[K,N : Numeric](key: K, counts: Map[K,N]): Map[K,N] = {
    val num = implicitly[Numeric[N]]
    val count = counts.getOrElse(key,num.zero)
    counts.updated(key,num.plus(count,num.one))
  }
}
