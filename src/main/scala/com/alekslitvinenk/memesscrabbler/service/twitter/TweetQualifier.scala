package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

trait TweetQualifier {
  
  def getQualifiedTweet(t: Tweet): Option[Tweet]
}
