package com.hochgi.json

// import is nested & FQCNs because of:
// https://github.com/plokhotnyuk/jsoniter-scala#known-issues
object JsoniterCodec {

  import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec

  implicit val eventCodec: JsonValueCodec[Event] = {
    com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.make[Event](
      com.github.plokhotnyuk.jsoniter_scala.macros.CodecMakerConfig(fieldNameMapper = {
      case "eventType" => "event_type"
      case anythingElse => anythingElse
    }))
  }

  implicit val statsCodec: JsonValueCodec[Stats] = {
    com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.make[Stats](
      com.github.plokhotnyuk.jsoniter_scala.macros.CodecMakerConfig()
    )
  }
}
