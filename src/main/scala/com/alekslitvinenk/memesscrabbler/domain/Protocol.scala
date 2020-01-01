package com.alekslitvinenk.memesscrabbler.domain

import spray.json.{DefaultJsonProtocol, JsArray, JsValue, RootJsonFormat}

object Protocol extends DefaultJsonProtocol{
  
  case class Media(
    id: String,
    mediaUrl: String,
    `type`: String,
  )
  
  case class EntitiesList(
    //hashTags: List[String],
    //urls: List[String],
    media: Option[List[Media]],
  )
  
  case class Tweet(
    createdAt: String,
    id: String,
    text: String,
    entities: EntitiesList,
  )
  
  private def readMedia(value: JsValue): Media = {
    val fields = value.asJsObject.fields
    
    Media(
      fields("id").toString,
      fields("media_url").toString,
      fields("type").toString
    )
  }
  
  private def readEntities(value: JsValue): EntitiesList = {
    val fields = value.asJsObject.fields
    
    EntitiesList(
      fields.get("media").map(_.asInstanceOf[JsArray].elements.map(readMedia).toList)
    )
  }
  
  implicit object TweetFormat extends RootJsonFormat[Tweet] {
    // The App ain't meant to produce JSON, so leave unimplemented
    def write(t: Tweet) = ???
    def read(value: JsValue): Tweet = {
      val fields = value.asJsObject.fields
      
      Tweet(
        createdAt = fields("created_at").toString,
        id        = fields("id").toString,
        text      = fields("text").toString,
        entities  = readEntities(fields("entities"))
      )
    }
  }
  
}
