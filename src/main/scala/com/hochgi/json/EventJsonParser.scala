package com.hochgi.json

import akka.util.ByteString
import com.github.plokhotnyuk.jsoniter_scala.core.{WriterConfig, readFromByteBuffer, writeToArray}
import com.hochgi.json.JsoniterCodec.{eventCodec,statsCodec}
import com.hochgi.util.incrementByKey

import scala.util.Try

object EventJsonParser {

  final case class Event(eventType: String, data: String, timestamp: Long)
  final case class Stats(eventTypesCount: Map[String,Long], wordsCount: Map[String,Long], failCount: Long) {

    def update(event: Try[Event]): Stats = event.fold(_ => this.copy(failCount = failCount + 1), {

      case Event(eventType,data,_) =>

        val newEventTypesCount = incrementByKey(eventType,eventTypesCount)
        val newWordsTypesCount = incrementByKey(data,wordsCount)

        Stats(newEventTypesCount,newWordsTypesCount,failCount)
    })
  }

  def parse(line: ByteString): Try[Event] = Try(readFromByteBuffer[Event](line.toByteBuffer))

  def format(stats: Stats, pretty: Boolean): Array[Byte] =
    if (pretty) writeToArray(stats, WriterConfig(indentionStep = 2))
    else writeToArray(stats)
}
