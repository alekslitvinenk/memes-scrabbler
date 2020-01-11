package com.alekslitvinenk.memesscrabbler.service.twitter

import com.alekslitvinenk.memesscrabbler.domain.Protocol.Mem
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet
import com.alekslitvinenk.memesscrabbler.service.persistance.MemStore

case class MemTweetProcessor(tweetGrader: TweetGrader, memStore: MemStore) {
  
  def process(t: Tweet): Unit =
    qualifyAsMem(t).map(memStore.storeMem)
  
  private def qualifyAsMem(t: Tweet): Option[Mem] =
    tweetGrader.gradeTweet(t).map { qualifiedTweet =>
      Mem(1)
    }
}
