package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.{Tweet, TweetLang}

case class MediaRetweetCountAndLangBasedQualifier(retweetCountThreshold: Int, maxVideoDurationMin: Int, lang: TweetLang.Value)
  extends MediaBasedQualifier(maxVideoDurationMin * 60 * 1000) {
  
  require(retweetCountThreshold >= 0)
  
  override def getQualifiedTweet(t: Protocol.Tweet): Option[Tweet] =
    super.getQualifiedTweet(t).flatMap { mediaTweet =>
      if (mediaTweet.retweetCount >= retweetCountThreshold && mediaTweet.lang == lang) Some(mediaTweet)
      else None
    }
}
