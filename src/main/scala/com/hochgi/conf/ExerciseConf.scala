package com.hochgi.conf

import org.rogach.scallop._

class ExerciseConf(arguments: Seq[String]) extends ScallopConf(arguments) {

  val port: ScallopOption[Int] = opt[Int](
    name = "port",
    short = 'p',
    default = Some(1729),
    descr = "Which port to use for the server (DEFAULT 1729)"
  )

  val maximumLineLength: ScallopOption[Int] = opt[Int](
    name = "max-line-length",
    short = 'm',
    default = Some(8192),
    descr = "Maximum length of lines coming out from generator (DEFAULT 8192)"
  )

  verify()
}
