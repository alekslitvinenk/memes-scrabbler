package com.alekslitvinenk.memesscrabbler.service.persistance
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.domain.Protocol
import com.alekslitvinenk.memesscrabbler.domain.Protocol.Mem
import com.alekslitvinenk.memesscrabbler.domain.twitter.BearerTokenProvider
import com.alekslitvinenk.memesscrabbler.util.StrictLogging
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.gridfs.{AsyncInputStream, GridFSBucket, GridFSUploadOptions}
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper._

import scala.concurrent.{ExecutionContext, Future}

case class MongoStore(mongoHost: String, dbName: String)(implicit bearerTokenProvider: BearerTokenProvider,
                 actorSystem: ActorSystem,
                 ec: ExecutionContext,
                 actorMaterializer: ActorMaterializer) extends MemStore with StrictLogging {
  
  private val mongoClient: MongoClient = MongoClient(s"mongodb://$mongoHost")
  private val database: MongoDatabase = mongoClient.getDatabase(dbName)
  private val gridFSBucket: GridFSBucket = GridFSBucket(database)
  
  // Create some custom options
  private val options: GridFSUploadOptions = new GridFSUploadOptions().chunkSizeBytes(1024 * 1204).metadata(Document("type" -> "presentation"))
  
  override def storeMem(m: Protocol.Mem): Future[Unit] = {
    materializeMem(m).map(_ => ())
  }
  
  override def getMemesCount(): Int = 0
  
  private def materializeMem(m: Mem) = {
    val req = HttpRequest()
      .withUri(Uri(m.mediaUrl))
    
    logger.debug(s"Fetching ${req.getUri()}")
  
      Http().singleRequest(req).flatMap { response =>
        response.entity.dataBytes.runReduce(_ ++ _).flatMap { byteString =>
          val streamToUploadFrom = Observable[ByteBuffer](List(byteString.asByteBuffer))
          gridFSBucket.uploadFromObservable("file", streamToUploadFrom).toFuture()
        }
      }
  }
}
