package com.alekslitvinenk.memesscrabbler.service.persistance

import com.alekslitvinenk.memesscrabbler.domain.Protocol.Mem

import scala.concurrent.Future

trait MemStore {
  
  def storeMem(m: Mem): Future[Unit]
  
  def getMemesCount(): Int
}
