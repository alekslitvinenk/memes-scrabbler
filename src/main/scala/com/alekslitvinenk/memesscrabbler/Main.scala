package com.alekslitvinenk.memesscrabbler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri}
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.typesafe.config.ConfigFactory

object Main extends App {

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
    .flatMap( resp => {
      resp.entity.dataBytes.runReduce(_ ++ _).map(f => println(f.utf8String))
    })

  println(memesScrabblerConfig)
  println(res)
}
