package com.alekslitvinenk.memesscrabbler.service

import com.alekslitvinenk.memesscrabbler.domain.Mem

import scala.concurrent.Future

trait MemStore {
  
  def storeMem(m: Mem): Future[Unit]
}
