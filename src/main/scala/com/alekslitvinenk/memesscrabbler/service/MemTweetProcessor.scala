package com.alekslitvinenk.memesscrabbler.service

import com.alekslitvinenk.memesscrabbler.domain.Mem
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.Tweet

case class MemTweetProcessor(tweetGrader: TweetGrader, memStore: MemStore) {
  
  def process(t: Tweet): Unit =
    qualifyAsMem(t).map(memStore.storeMem)
  
  private def qualifyAsMem(t: Tweet): Option[Mem] =
    if (tweetGrader.gradeTweet(t)) Some(Mem(1))
    else None
}
