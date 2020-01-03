package com.alekslitvinenk.memesscrabbler.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.domain.Protocol._

import scala.concurrent.{ExecutionContext, Future}

case class TwitterAccount(twitterId: TwitterId)
                    (implicit bearerTokenProvider: BearerTokenProvider,
                     actorSystem: ActorSystem,
                     ec: ExecutionContext,
                     actorMaterializer: ActorMaterializer) extends SprayJsonSupport {
  
  def consumeFeed(f: Tweet => Unit): Future[List[Unit]] = {
    def queryAndConsumeChunk(startFromTweetId: Option[Long] = None): Future[List[Unit]] =
      queryTimeline(startFromTweetId).flatMap(tweets => {
        
        val tweetsToProcess = if (startFromTweetId.isDefined)
          tweets.drop(1)
        else tweets
  
        tweetsToProcess.map(f)
        
        if(tweets.length > 1)
          queryAndConsumeChunk(Some(tweets.last.id))
        else Future.successful(List.empty)
      })
    
    queryAndConsumeChunk()
  }
  
  private def queryTimeline(startFromTweetId: Option[Long] = None): Future[List[Tweet]] = {
    val queryParams = Map(
      "user_id" -> twitterId.value,
      "include_rts" -> "false"
    ) ++ startFromTweetId.map("max_id" -> _.toString)
    
    println(s">>> $queryParams")
    
    val uri = Uri("https://api.twitter.com/1.1/statuses/user_timeline.json")
      .withQuery(Query(queryParams))
  
    val req = HttpRequest()
      .withUri(uri)
      .withHeaders(Authorization(OAuth2BearerToken(bearerTokenProvider.getCurrentToken.value)))
  
    Http().singleRequest(req).flatMap { response =>
      println(s">>> ${response.status}")
      Unmarshal(response).to[List[Tweet]]
    }
  }
}
