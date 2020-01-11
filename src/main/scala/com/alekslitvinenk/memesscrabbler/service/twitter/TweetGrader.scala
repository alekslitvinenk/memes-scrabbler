package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

trait TweetGrader {
  
  def gradeTweet(t: Tweet): Option[Tweet]
}
