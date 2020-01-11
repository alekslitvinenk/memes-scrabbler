package com.alekslitvinenk.memesscrabbler.domain.twitter

import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol.MediaType
import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsValue, JsonReader, RootJsonFormat}

object Protocol extends DefaultJsonProtocol {
  
  case class VideoVariant(
    bitRate    : Option[Int],
    contentType: String,
    url        : String,
  )
  
  case class VideoInfo(
    aspectRation: List[Int],
    variants    : List[VideoVariant],
    durationMills: Option[Int],
  )
  
  object MediaType extends Enumeration {
    val Photo, Video = Value
  }
  
  case class Media(
    id: Long,
    mediaUrl: String,
    `type`: MediaType.Value,
    videoInfo: Option[VideoInfo],
  )
  
  case class EntitiesList(
    media   : Option[List[Media]],
  )
  
  case class Tweet(
    createdAt: String,
    id: Long,
    text: String,
    extendedEntities: Option[EntitiesList],
    lang: String,
    retweetCount: Int,
    favouriteCont: Int,
  )
  
  private def readVideoVariant(value: JsValue): VideoVariant = {
    val fields = value.asJsObject.fields
    
    VideoVariant(
      bitRate = fields.get("bitrate").map(_.convertTo[Int]),
      contentType = fields("content_type").convertTo[String],
      url = fields("url").convertTo[String],
    )
  }
  
  private def readVideoInfo(value: JsValue): VideoInfo = {
    val fields = value.asJsObject.fields
    
    VideoInfo(
      aspectRation = fields("aspect_ratio").asInstanceOf[JsArray].elements.map(_.asInstanceOf[JsNumber].value.toInt).toList,
      variants = fields("variants").asInstanceOf[JsArray].elements.map(readVideoVariant).toList,
      durationMills = fields.get("duration_millis").map(_.convertTo[Int]),
    )
  }
  
  implicit val readMediaType: JsonReader[MediaType.Value] = (json: JsValue) => json.convertTo[String] match {
    case "photo" => MediaType.Photo
    case "video" => MediaType.Video
    case _ => throw new IllegalArgumentException("Unknown media type")
  }
  
  private def readMedia(value: JsValue): Media = {
    val fields = value.asJsObject.fields
    
    Media(
      id = fields("id").convertTo[Long],
      mediaUrl = fields("media_url").convertTo[String],
      `type` = fields("type").convertTo[MediaType.Value],
      videoInfo = fields.get("video_info").map(readVideoInfo)
    )
  }
  
  private def readEntities(value: JsValue): EntitiesList = {
    val fields = value.asJsObject.fields
    
    EntitiesList(
      media = fields.get("media").map(_.asInstanceOf[JsArray].elements.map(readMedia).toList)
    )
  }
  
  implicit object TweetFormat extends RootJsonFormat[Tweet] {
    // The App ain't meant to produce JSON, so leave unimplemented
    def write(t: Tweet) = ???
    
    def read(value        : JsValue): Tweet = {
      val fields = value.asJsObject.fields
      
      Tweet(
        createdAt = fields("created_at").convertTo[String],
        id = fields("id").convertTo[Long],
        text = fields("text").convertTo[String],
        extendedEntities = fields.get("extended_entities").map(readEntities),
        lang = fields("lang").convertTo[String],
        retweetCount = fields("retweet_count").convertTo[Int],
        favouriteCont = fields("favorite_count").convertTo[Int],
      )
    }
  }
}
