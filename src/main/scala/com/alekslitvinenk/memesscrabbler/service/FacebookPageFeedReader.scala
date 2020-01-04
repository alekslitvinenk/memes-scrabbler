package com.alekslitvinenk.memesscrabbler.service

import com.alekslitvinenk.memesscrabbler.domain.facebook.PageId
import com.alekslitvinenk.memesscrabbler.domain.facebook.Protocol._

import scala.concurrent.Future

case class FacebookPageFeedReader(pageId: PageId) {
  
  // Dummy implementation
  def consumeFeed(f: FacebookPost => Unit): Future[Unit] =
    Future.successful(())
}
