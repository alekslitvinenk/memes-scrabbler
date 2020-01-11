package com.alekslitvinenk.memesscrabbler.service.persistance

import com.alekslitvinenk.memesscrabbler.domain.Mem
import com.alekslitvinenk.memesscrabbler.util.StrictLogging

import scala.concurrent.Future

class MemStoreStub() extends MemStore with StrictLogging {
  override def storeMem(m: Mem): Future[Unit] = {
    logger.info(s"Persisting $m to nowhere")
    Future.successful(())
  }
}

object MemStoreStub {
  def apply(): MemStoreStub = new MemStoreStub()
}
