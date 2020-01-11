package com.alekslitvinenk.memesscrabbler.service.facebook

import com.alekslitvinenk.memesscrabbler.domain.facebook.PageId
import com.alekslitvinenk.memesscrabbler.domain.facebook.Protocol.FacebookPost

import scala.concurrent.Future

case class FacebookPageFeedReader(pageId: PageId) {
  
  // Dummy implementation
  def consumeFeed(f: FacebookPost => Unit): Future[Unit] =
    Future.successful(())
}
