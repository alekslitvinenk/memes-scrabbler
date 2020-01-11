package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

case class MediaRetweetCountBasedQualifier(retweetCountThreshold: Int, maxVideoDurationMin: Int)
  extends MediaBasedQualifier(maxVideoDurationMin * 60 * 1000) {
  
  require(retweetCountThreshold >= 0)
  
  override def getQualifiedTweet(t: Protocol.Tweet): Option[Tweet] =
    super.getQualifiedTweet(t).flatMap { mediaTweet =>
      if (t.retweetCount >= retweetCountThreshold) Some(mediaTweet)
      else None
    }
}
