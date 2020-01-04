package com.alekslitvinenk.memesscrabbler.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol._
import com.alekslitvinenk.memesscrabbler.domain.twitter.{BearerTokenProvider, TwitterId}

import scala.concurrent.{ExecutionContext, Future}

case class TwitterAccountReader(twitterId: TwitterId)
                               (implicit bearerTokenProvider: BearerTokenProvider,
                                actorSystem: ActorSystem,
                                ec: ExecutionContext,
                                actorMaterializer: ActorMaterializer) extends SprayJsonSupport {
  
  def consumeTweets(f: Tweet => Unit): Future[List[Unit]] = {
    queryAndConsumeByChunk(f)
  }
  
  private def queryAndConsumeByChunk(f: Tweet => Unit, startFromTweetId: Option[Long] = None): Future[List[Unit]] =
    queryTimeline(startFromTweetId).flatMap(tweets => {
      
      val tweetsToProcess = if (startFromTweetId.isDefined)
        tweets.drop(1)
      else tweets
      
      tweetsToProcess.foreach(f)
      
      if (tweets.length > 1)
        queryAndConsumeByChunk(f, Some(tweets.last.id))
      else Future.successful(List.empty)
    })
  
  private def queryTimeline(startFromTweetId: Option[Long] = None): Future[List[Tweet]] = {
    val queryParams = Map(
      "screen_name" -> twitterId.value,
      "include_rts" -> "false"
    ) ++ startFromTweetId.map("max_id" -> _.toString)
    
    println(s">>> $queryParams")
    
    val uri = Uri("https://api.twitter.com/1.1/statuses/user_timeline.json")
      .withQuery(Query(queryParams))
    
    val currentBearerToken = bearerTokenProvider.getCurrentToken
    
    val req = HttpRequest()
      .withUri(uri)
      .withHeaders(Authorization(OAuth2BearerToken(currentBearerToken.value)))
    
    Http().singleRequest(req).flatMap { response =>
      println(s">>> ${response.status}")
      
      response.status match {
        case OK => Unmarshal(response).to[List[Tweet]]
        
        case TooManyRequests =>
          // shift bearer token if rate limit was met
          bearerTokenProvider.setNextCurrentToken(currentBearerToken)
          // try with new token
          queryTimeline(startFromTweetId)
        
        case _ => Future.successful(List.empty)
      }
    }
  }
}
