package com.alekslitvinenk.memesscrabbler.service
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol

case class RetweetsBasedTweetGrader(retweetsThreshold: Int) extends TweetGrader {
  
  require(retweetsThreshold >= 0)
  
  override def gradeTweet(t: Protocol.Tweet): Boolean = false
}