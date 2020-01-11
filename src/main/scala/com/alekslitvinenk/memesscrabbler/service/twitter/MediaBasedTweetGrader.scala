package com.alekslitvinenk.memesscrabbler.service.twitter
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol._

class MediaBasedTweetGrader extends TweetGrader {
  override def gradeTweet(t: Tweet): Option[Tweet] = t.extendedEntities match {
    case Some(EntitiesList(Some(_))) => Some(t)
    case _ => None
  }
}
