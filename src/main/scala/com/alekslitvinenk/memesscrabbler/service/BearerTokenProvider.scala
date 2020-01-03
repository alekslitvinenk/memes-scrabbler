package com.alekslitvinenk.memesscrabbler.service

// Helps work around rate limit by providing next bearer token when the current one is exhausted
class BearerTokenProvider(tokens: List[BearerToken]) {
  
  def getCurrentToken: BearerToken = ???
  
  def setNextCurrentToken: Unit = ???
}
