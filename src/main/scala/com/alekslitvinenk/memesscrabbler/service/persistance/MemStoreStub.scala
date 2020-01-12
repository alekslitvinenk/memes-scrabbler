package com.alekslitvinenk.memesscrabbler.service.persistance

import java.util.concurrent.atomic.AtomicInteger

import com.alekslitvinenk.memesscrabbler.domain.Protocol.Mem
import com.alekslitvinenk.memesscrabbler.util.StrictLogging

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

class MemStoreStub() extends MemStore with StrictLogging {
  
  private val memCounter: AtomicInteger = new AtomicInteger(0)
  private val store: TrieMap[Int, Mem] = TrieMap.empty
  
  override def storeMem(m: Mem): Future[Unit] = {
    logger.debug(s"Persisting $m to nowhere")
    store += ((memCounter.incrementAndGet(), m))
    Future.successful(())
  }
  
  def getMemesCount: Int = memCounter.get()
}

object MemStoreStub {
  def apply(): MemStoreStub = new MemStoreStub()
}
