package com.hochgi.json

import akka.util.ByteString
import com.hochgi.util.incrementByKey
import upickle.default._

import scala.util.Try

object EventJsonParser {

  object           Event { implicit val rw: ReadWriter[Event] = macroRW }
  final case class Event(@upickle.implicits.key("event_type") eventType: String, data: String, timestamp: Long)

  object           Stats { implicit val rw: ReadWriter[Stats] = macroRW }
  final case class Stats(eventTypesCount: Map[String,Long], wordsCount: Map[String,Long], failCount: Long) {

    def update(event: Try[Event]): Stats = event.fold(_ => this.copy(failCount = failCount + 1), {

      case Event(eventType,data,_) =>

        val newEventTypesCount = incrementByKey(eventType,eventTypesCount)
        val newWordsTypesCount = incrementByKey(data,wordsCount)

        Stats(newEventTypesCount,newWordsTypesCount,failCount)
    })
  }

  def parse(line: ByteString): Try[Event] = Try(read[Event](line.utf8String))

  def format(stats: Stats, pretty: Boolean): String =
    if (pretty) writeJs(stats).render(indent = 2)
    else write(stats)
}
