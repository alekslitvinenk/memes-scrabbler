package com.alekslitvinenk.memesscrabbler.domain.twitter

import org.scalatest.{Matchers, WordSpec}

class BearerTokenProviderSpec extends WordSpec with Matchers {
  
  val bearerToken1 = BearerToken("1")
  val bearerToken2 = BearerToken("2")
  
  val bearerTokenProvider = BearerTokenProvider(List(bearerToken1, bearerToken2))
  
  "BearerTokenProvider" should {
    "return bearerToken1 when getCurrentToken called" in {
      val currentToken = bearerTokenProvider.getCurrentToken
      
      currentToken should be(bearerToken1)
    }
  
    "return bearerToken2 when getCurrentToken called after setNextCurrentToken" in {
      bearerTokenProvider.setNextCurrentToken(bearerToken1)
      
      val currentToken = bearerTokenProvider.getCurrentToken
      
      currentToken should be(bearerToken2)
    }
  }
}
