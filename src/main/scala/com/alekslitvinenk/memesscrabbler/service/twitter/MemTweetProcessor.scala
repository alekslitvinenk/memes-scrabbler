package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.Protocol._
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.{MediaType => TwitterMediaType, _}
import com.alekslitvinenk.memesscrabbler.service.persistance.MemStore

case class MemTweetProcessor(tweetQualifier: TweetQualifier, memStore: MemStore) {
  
  def process(t: Tweet): Unit =
    qualifyAsMem(t).map(memStore.storeMem)
  
  private def qualifyAsMem(t: Tweet): Option[Mem] =
    tweetQualifier.getQualifiedTweet(t).map { qualifiedTweet =>
      
      // We assume that qualified tweet has all this chain of properties set to Some value
      qualifiedTweet.extendedEntities.get.media.get.head match {
        case Media(_, mediaUrl, TwitterMediaType.Photo, None) => Mem(mediaUrl, MediaType.Image)
        case Media(_, _, TwitterMediaType.Video, Some(videoInfo)) =>
          Mem(
            // TODO: pick the variant with the lowest bitrate
            videoInfo.variants.head.url,
            MediaType.Clip
          )
      }
    }
}
