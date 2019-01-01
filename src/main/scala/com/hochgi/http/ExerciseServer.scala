package com.hochgi.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Framing, Keep, Sink}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer}
import com.hochgi.conf.ExerciseConf
import com.hochgi.json.EventJsonParser
import com.hochgi.json.EventJsonParser.Stats
import com.hochgi.stream.ExerciseGenerator
import com.hochgi.util._

import scala.concurrent.ExecutionContext

object ExerciseServer extends App {

  val conf = new ExerciseConf(args)

  implicit val system: ActorSystem = ActorSystem("http-generator-stream")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  // volatile var with immutable value is a perfectly good use in this scenario
  // we only write from one source of input, which never reads the state
  // state is internally saved within scan combinator, and we just override
  // the stats var with new data.
  // we can read it from any thread, no need to worry about synchronization.
  // we can even remove volatile modifier, which will result in better performance,
  // since not reading directly from memory, at the cost of occasionally
  // reading a slightly outdated value, if not yet propagated to cache
  @volatile private[this] var stats: Stats = Stats(Map.empty, Map.empty, 0L)

  // build the stream reading generator output
  // store kill switch materialize value for graceful shutdown
  val ks = ExerciseGenerator
    .fromResource("generator-linux-amd64")
    .via(Framing.delimiter(endln, maximumFrameLength = conf.maximumLineLength()))
    .map(EventJsonParser.parse)
    .scan(Stats(Map.empty, Map.empty, 0L))(_ update _)
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.foreach { stats = _ })(Keep.left)
    .run()

  // simple route definition
  val route = path("stats") {
    get {
      parameters('pretty.?) { prettyOpt =>
        complete {
          HttpEntity(
            ContentTypes.`application/json`,
            EventJsonParser.format(stats, prettyOpt.fold(false)("false".!=)))
        }
      }
    }
  }

  // binding requested port
  val bindingFuture = Http().bindAndHandle(route, "localhost", conf.port())

  // shameless C&P from akka-http docs:
  //   https://doc.akka.io/docs/akka-http/current/routing-dsl/index.html#minimal-example
  println(s"Server online at http://localhost:${conf.port()}/\nPress RETURN to stop...")
  scala.io.StdIn.readLine() // let it run until user presses return
  ks.shutdown()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
