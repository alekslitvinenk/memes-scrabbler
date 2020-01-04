package com.alekslitvinenk.memesscrabbler.domain.twitter

import java.util.concurrent.atomic.AtomicReference

import scala.collection.mutable

// Helps work around rate limit by providing next bearer token when the current one gets exhausted
case class BearerTokenProvider(tokens: List[BearerToken]) {
  
  // Passing list as a vararg
  private val tokensQueue: mutable.Queue[BearerToken] = mutable.Queue[BearerToken](tokens: _*)
  private val tokenRef: AtomicReference[BearerToken] = new AtomicReference[BearerToken](tokensQueue.dequeue())
  
  def getCurrentToken: BearerToken = tokenRef.get()
  
  def setNextCurrentToken(currentToken: BearerToken): Any = {
    
    val head = tokensQueue.head
    
    if (tokenRef.compareAndSet(currentToken, tokensQueue.head)) {
      tokensQueue.dequeue()
      tokensQueue.enqueue(currentToken)
    }
  }
}
