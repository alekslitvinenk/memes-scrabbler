package com.alekslitvinenk.memesscrabbler

import com.alekslitvinenk.memesscrabbler.config.MemesScrabbler
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val memesScrabblerConfig = MemesScrabbler(config)

  println(memesScrabblerConfig)
}
