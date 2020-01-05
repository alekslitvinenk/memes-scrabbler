package com.alekslitvinenk.memesscrabbler.util

import org.slf4j.{Logger, LoggerFactory}

trait StrictLogging {
  protected val logger: Logger =
    LoggerFactory.getLogger(getClass.getName)
}
