package com.hochgi.json

import com.hochgi.json.EventJsonParser.{Event, Stats}

// imports are nested because of:
// https://github.com/plokhotnyuk/jsoniter-scala#known-issues
object JsoniterCodec {

  import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec

  implicit val eventCodec: JsonValueCodec[Event] = {
    import com.github.plokhotnyuk.jsoniter_scala.macros.{JsonCodecMaker, CodecMakerConfig}
    JsonCodecMaker.make[Event](CodecMakerConfig.apply(fieldNameMapper = {
      case "eventType" => "event_type"
      case anythingElse => anythingElse
    }))
  }

  implicit val statsCodec: JsonValueCodec[Stats] = {
    import com.github.plokhotnyuk.jsoniter_scala.macros.{JsonCodecMaker, CodecMakerConfig}
    JsonCodecMaker.make[Stats](CodecMakerConfig())
  }
}
