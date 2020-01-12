package com.alekslitvinenk.memesscrabbler.domain.twitter

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsValue, JsonReader, RootJsonFormat}

import scala.util.Try

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
    val Photo, Video, Unknown = Value
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
    lang: TweetLang.Value,
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
  
  implicit val readMediaType: JsonReader[MediaType.Value] = (json: JsValue) => Try {
    val mediaType = json.convertTo[String]
    mediaType match {
      case "photo" => MediaType.Photo
      case "video" => MediaType.Video
      case _ => throw new IllegalArgumentException(s"Unknown media type: $mediaType")
    }
  }.getOrElse(MediaType.Unknown)
  
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
  
  object TweetLang extends Enumeration {
    val Ru = Value("ru")
    val En = Value("en")
    val De = Value("de")
    val Fr = Value("fr")
    val Unknown = Value
  }
  
  implicit val readTweetLang: JsonReader[TweetLang.Value] = (json: JsValue) => {
    Try(TweetLang.withName(json.convertTo[String])).getOrElse(TweetLang.Unknown)
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
        lang = fields("lang").convertTo[TweetLang.Value],
        retweetCount = fields("retweet_count").convertTo[Int],
        favouriteCont = fields("favorite_count").convertTo[Int],
      )
    }
  }
}
