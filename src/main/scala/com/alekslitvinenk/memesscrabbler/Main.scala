package com.alekslitvinenk.memesscrabbler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.alekslitvinenk.memesscrabbler.domain.Protocol._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main extends App with SprayJsonSupport{
  
  implicit val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val memesScrabblerConfig = MemesScrabbler(config)

  val queryParams = Map(
    "user_id" -> memesScrabblerConfig.twitterId
  )
  val query = Query(queryParams)
  val uri = Uri("https://api.twitter.com/1.1/statuses/user_timeline.json")
    .withQuery(Query(queryParams))

  val req = HttpRequest()
    .withUri(uri)
    .withHeaders(Authorization(OAuth2BearerToken(memesScrabblerConfig.twitterBearerToken)))

  val res = Http().singleRequest(req)
  
  val response = Await.result(res, Duration.Inf)
  
  // unmarshal:
  val unmarshalled: Future[List[Tweet]] =
    Unmarshal(response).to[List[Tweet]]
  
  val errr = Await.result(unmarshalled, Duration.Inf)
  
  println()
}
