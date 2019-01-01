package com.hochgi.stream

import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.util.ByteString
import com.hochgi.util.endln
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.Queue
import scala.sys.process._

object ExerciseGenerator {

  /**
    * @param path - file path
    */
  def apply(path: String): Source[ByteString,NotUsed] = {
    Source
      .fromGraph(new ExerciseGenerator(path))
      .map(s => ByteString(s,StandardCharsets.UTF_8) ++ endln)
  }

  /**
    * @param name - resource name
    */
  def fromResource(name: String):  Source[ByteString,NotUsed] = {
    val sb = new StringBuilder
    sb ++= name.reverse
    sb ++= "resources/".reverse
    getClass
      .getProtectionDomain
      .getCodeSource
      .getLocation
      .toURI
      .getPath
      .reverseIterator
      .dropWhile('/'.!=)
      .drop("lib/".length)
      .foreach(sb.+=)

    apply(sb.reverseContents().result())
  }
}

class ExerciseGenerator private(path: String) extends GraphStage[SourceShape[String]] with LazyLogging {
  val out: Outlet[String] = Outlet("ExerciseGenerator.out")
  override val shape: SourceShape[String] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with OutHandler {

    private[this] var asyncHandler: String => Unit = _
    private[this] var buffer: Queue[String] = Queue.empty[String]
    private[this] var process: Process = _

    override def preStart() = {
      val ac = getAsyncCallback[String] { str =>
        buffer = buffer.enqueue(str)
        if (isAvailable(out)) {
          val (next,q) = buffer.dequeue
          push(out, next)
          buffer = q
        }
      }
      asyncHandler = ac.invoke

      process = Process(path).run(new ProcessLogger {
        override def out(s: => String): Unit = asyncHandler(s)
        override def err(s: => String): Unit = logger.error("generator [STDERR]: " + s)
        override def buffer[T](f: => T): T = f
      })
    }

    def onPull(): Unit = if(buffer.nonEmpty){
      val (next,q) = buffer.dequeue
      push(out,next)
      buffer = q
    }

    override def onDownstreamFinish(): Unit = {
      process.destroy()
      super.onDownstreamFinish()
    }

    setHandler(out, this)
  }
}