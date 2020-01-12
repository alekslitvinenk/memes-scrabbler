package com.alekslitvinenk.memesscrabbler.service.persistance
import com.alekslitvinenk.memesscrabbler.domain.Protocol

import scala.concurrent.Future

class MongoStore extends MemStore {
  
  override def storeMem(m: Protocol.Mem): Future[Unit] = ???
}
