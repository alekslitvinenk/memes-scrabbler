package com.alekslitvinenk.memesscrabbler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.alekslitvinenk.memesscrabbler.domain.facebook.PageId
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.TweetLang
import com.alekslitvinenk.memesscrabbler.domain.twitter.{BearerToken, BearerTokenProvider, TwitterId}
import com.alekslitvinenk.memesscrabbler.service.facebook.FacebookPageFeedReader
import com.alekslitvinenk.memesscrabbler.service.persistance.MongoStore
import com.alekslitvinenk.memesscrabbler.service.twitter.{MediaRetweetCountAndLangBasedQualifier, MemTweetProcessor, TwitterAccountReader}
import com.alekslitvinenk.memesscrabbler.util.{MetricsUtil, StrictLogging}
import com.typesafe.config.ConfigFactory
import io.prometheus.client.hotspot.DefaultExports

import scala.concurrent.Future

object Main extends App with StrictLogging {
  
  val TwitterAccount = "t"
  val FacebookPage = "f"
  
  // Registers default Prometheus exporters
  DefaultExports.initialize()
  
  implicit val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val memesScrabblerConfig = MemesScrabbler(config)
  val resourcesList = memesScrabblerConfig.resourceIds.split(",")

  implicit val bearerTokenProvider = BearerTokenProvider(
    memesScrabblerConfig.twitterBearerTokens.split(",").map(bt => BearerToken(bt)).toList
  )
  
  val memStore = MongoStore(memesScrabblerConfig.mongoHost, memesScrabblerConfig.mongoDb)
  
  val futureResults = resourcesList.map { r =>
    val prefix = r.substring(0, 1)
    val id = r.substring(2)
    
    prefix match {
      case TwitterAccount => TwitterAccountReader(TwitterId(id))
        .consumeTweets(MemTweetProcessor(
          MediaRetweetCountAndLangBasedQualifier(
            memesScrabblerConfig.retweetsToQualify,
            memesScrabblerConfig.maxVideoDurationMin,
            TweetLang.withName(memesScrabblerConfig.memLanguage),
          ),
          memStore).process)

      // FacebookPageFeedReader here just for app extensibility demonstration
      // It has dummy implementation for the time being ;)
      case FacebookPage => FacebookPageFeedReader(PageId(id))
        .consumeFeed(_ => ())
    }
  }.toList
  
  val finalFuture =
    if (futureResults.nonEmpty) Future.reduceLeft(futureResults)((_, _) => ())
    else Future.successful(())
  
  finalFuture.onComplete { f =>
    f.fold(e => logger.error(s"Something's gone wrong: $e"), _ => {
      logger.debug(">>> All jobs completed")
      logger.debug(s"Memes collected: ${memStore.getMemesCount}")
    })
  }
  
  // Serve metrics
  val route =
    path("metrics") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, MetricsUtil.getPlainText))
      }
    }
  
  Http().bindAndHandle(route, "0.0.0.0",8080)
}
