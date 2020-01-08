package com.alekslitvinenk.memesscrabbler.service

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

trait TweetGrader {
  
  def gradeTweet(t: Tweet): Boolean
}
