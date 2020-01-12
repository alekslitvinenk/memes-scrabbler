package com.alekslitvinenk.memesscrabbler.domain

object Protocol {
  
  object MediaType extends Enumeration {
    val Image, Clip = Value
  }
  
  case class Mem(
    mediaUrl: String,
    mediaType: MediaType.Value
  )
}
