package com.alekslitvinenk.memesscrabbler.service.persistance
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.domain.Protocol
import com.alekslitvinenk.memesscrabbler.domain.twitter.BearerTokenProvider
import com.alekslitvinenk.memesscrabbler.util.StrictLogging
import org.mongodb.scala.gridfs.{GridFSBucket, GridFSUploadOptions}
import org.mongodb.scala.model.{IndexOptions, Indexes}
import org.mongodb.scala.{Completed, Document, MongoClient, MongoDatabase, Observable, Observer}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MongoStore(mongoHost: String, dbName: String)(implicit bearerTokenProvider: BearerTokenProvider,
                 actorSystem: ActorSystem,
                 ec: ExecutionContext,
                 actorMaterializer: ActorMaterializer) extends MemStore with StrictLogging {
  
  private val mongoClient: MongoClient = MongoClient(s"mongodb://$mongoHost")
  private val database: MongoDatabase = mongoClient.getDatabase(dbName)
  private val gridFSBucket: GridFSBucket = GridFSBucket(database)
  private val filesCollection = database.getCollection("fs.files")
  filesCollection.createIndex(Indexes.ascending("md5"), new IndexOptions().unique(true)).subscribe(SingleSubscriber())
  
  override def storeMem(m: Protocol.Mem): Future[Unit] = {
    val req = HttpRequest()
      .withUri(Uri(m.mediaUrl))
  
    logger.debug(s"Fetching ${req.getUri()}")
  
    Http().singleRequest(req).flatMap { response =>
      response.entity.dataBytes.runReduce(_ ++ _).flatMap { byteString =>
        
        val options: GridFSUploadOptions = new GridFSUploadOptions()
          .chunkSizeBytes(1024 * 1204)
          .metadata(Document("type" -> m.mediaType.toString))
        
        val streamToUploadFrom = Observable[ByteBuffer](List(byteString.asByteBuffer))
        
        gridFSBucket.uploadFromObservable("file", streamToUploadFrom, options).toFuture().map(_ => ())
      }
    }
  }
  
  override def getMemesCount(): Int = {
    val res = Await.result(gridFSBucket.find().toFuture(), Duration.Inf)
    res.length
  }
}

case class MongoCompleteSubscriber() extends Observer[Completed] with StrictLogging {
  override def onNext(result: Completed): Unit = logger.debug(s"Inserted $result")
  
  override def onError(e: Throwable): Unit = logger.error(s"Failed: $e")
  
  override def onComplete(): Unit = logger.debug("Completed")
}

case class SingleSubscriber() extends Observer[String]  with StrictLogging {
  override def onNext(result: String): Unit = logger.debug(result)
  
  override def onError(e: Throwable): Unit = logger.error(s"Failed: $e")
  
  override def onComplete(): Unit = logger.debug("Completed")
}
