package com.alekslitvinenk.memesscrabbler

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.alekslitvinenk.memesscrabbler.domain.facebook.PageId
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.TweetLang
import com.alekslitvinenk.memesscrabbler.domain.twitter.{BearerToken, BearerTokenProvider, TwitterId}
import com.alekslitvinenk.memesscrabbler.service.facebook.FacebookPageFeedReader
import com.alekslitvinenk.memesscrabbler.service.persistance.MongoStore
import com.alekslitvinenk.memesscrabbler.service.twitter.{MediaRetweetCountAndLangBasedQualifier, MemTweetProcessor, TwitterAccountReader}
import com.alekslitvinenk.memesscrabbler.util.StrictLogging
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main extends App with StrictLogging {
  
  val TwitterAccount = "t"
  val FacebookPage = "f"
  
  implicit val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val memesScrabblerConfig = MemesScrabbler(config)
  val resourcesList = memesScrabblerConfig.resourceIds.split(",")

  implicit val bearerTokenProvider = BearerTokenProvider(List(
    BearerToken(memesScrabblerConfig.twitterBearerToken),
    BearerToken("123"),
  ))
  
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
  
  Await.result(finalFuture, Duration.Inf)
  
  logger.debug(">>> All jobs completed")
  logger.debug(s"Memes collected: ${memStore.getMemesCount}")
  
  system.terminate();
}
