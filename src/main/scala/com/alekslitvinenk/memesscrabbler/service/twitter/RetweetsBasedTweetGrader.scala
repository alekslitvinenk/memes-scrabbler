package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

case class RetweetsBasedTweetGrader(retweetCountThreshold: Int) extends MediaBasedTweetGrader {
  
  require(retweetCountThreshold >= 0)
  
  override def gradeTweet(t: Protocol.Tweet): Option[Tweet] =
    super.gradeTweet(t).flatMap { mediaTweet =>
      if (t.retweetCount >= retweetCountThreshold) Some(mediaTweet)
      else None
    }
}
