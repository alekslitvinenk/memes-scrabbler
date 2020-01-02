package com.alekslitvinenk.memesscrabbler.domain

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsValue, RootJsonFormat}

object Protocol extends DefaultJsonProtocol{
  
  case class VideoVariant(
    bitRate: Option[Int],
    contentType: String,
    url: String,
  )
  
  case class VideoInfo(
    aspectRation: List[Int],
    variants: List[VideoVariant],
    durationMills: Int,
  )
  
  case class Media(
    id: String,
    mediaUrl: String,
    `type`: String,
    videoInfo: Option[VideoInfo],
  )
  
  case class EntitiesList(
    media: Option[List[Media]],
  )
  
  case class Tweet(
    createdAt: String,
    id       : String,
    text     : String,
    extendedEntities: Option[EntitiesList],
  )
  
  private def readVideoVariant(value: JsValue): VideoVariant = {
    val fields = value.asJsObject.fields
    
    VideoVariant(
      fields.get("bitrate").map(_.convertTo[Int]),
      fields("content_type").toString,
      fields("url").toString
    )
  }
  
  private def readVideoInfo(value: JsValue): VideoInfo = {
    val fields = value.asJsObject.fields
    
    VideoInfo(
      aspectRation = fields("aspect_ratio").asInstanceOf[JsArray].elements.map(_.asInstanceOf[JsNumber].value.toInt).toList,
      variants = fields("variants").asInstanceOf[JsArray].elements.map(readVideoVariant).toList,
      durationMills = fields("duration_millis").asInstanceOf[JsNumber].value.toInt,
    )
  }
  
  private def readMedia(value: JsValue): Media = {
    val fields = value.asJsObject.fields
    
    Media(
      id = fields("id").toString,
      mediaUrl = fields("media_url").toString,
      `type` = fields("type").toString,
      videoInfo = fields.get("video_info").map(readVideoInfo)
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
        extendedEntities  = fields.get("extended_entities").map(readEntities)
      )
    }
  }
  
}
