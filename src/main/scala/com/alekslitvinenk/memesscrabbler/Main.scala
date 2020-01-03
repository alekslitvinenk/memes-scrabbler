package com.alekslitvinenk.memesscrabbler

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.alekslitvinenk.memesscrabbler.service.{BearerToken, BearerTokenProvider, TwitterAccount, TwitterId}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  
  implicit val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val memesScrabblerConfig = MemesScrabbler(config)

  implicit val bearerTokenProvider = BearerTokenProvider(List(BearerToken(memesScrabblerConfig.twitterBearerToken)))
  
  val twitterAccount = TwitterAccount(TwitterId(memesScrabblerConfig.twitterId))
  
  val f = twitterAccount.consumeFeed(t => {
    println("====================================")
    println(t.text)
  })
  
  Await.result(f, Duration.Inf)
}
