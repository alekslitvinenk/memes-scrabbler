package com.alekslitvinenk.memesscrabbler.util

import java.io.StringWriter

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

object MetricsUtil {
  
  def getPlainText: String = {
    val writer = new StringWriter()
    TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
    writer.toString
  }
}
