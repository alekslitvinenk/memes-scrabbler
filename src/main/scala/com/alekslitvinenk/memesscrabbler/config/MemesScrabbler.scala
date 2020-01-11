package com.alekslitvinenk.memesscrabbler.config

import com.typesafe.config.Config
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class MemesScrabbler(
   resourceIds: String,
   twitterBearerToken: String,
   retweetsToQualify: Int,
   maxVideoDurationMin: Int,
   memLanguage: String,
)

object MemesScrabbler {
  def apply(config: Config): MemesScrabbler = {
    ConfigSource.fromConfig(config).at("memes-scrabbler").loadOrThrow[MemesScrabbler]
  }
}
