package com.alekslitvinenk.memesscrabbler.service
import com.alekslitvinenk.memesscrabbler.domain.Mem

import scala.concurrent.Future

class MemStoreStub() extends MemStore {
  override def storeMem(m: Mem): Future[Unit] = Future.successful(())
}

object MemStoreStub {
  def apply(): MemStoreStub = new MemStoreStub()
}
