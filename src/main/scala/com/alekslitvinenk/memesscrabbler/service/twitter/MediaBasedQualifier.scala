package com.alekslitvinenk.memesscrabbler.service.twitter
import com.alekslitvinenk.memesscrabbler.domain.twitter.Protocol._

class MediaBasedQualifier(maxVideoDurationMs: Int) extends TweetQualifier {
  
  override def getQualifiedTweet(t: Tweet): Option[Tweet] = t.extendedEntities match {
    case Some(EntitiesList(Some(mediaList))) => {
      
      mediaList.head match {
        case Media(_, _, MediaType.Photo, _) => Some(t)
        case Media(_, _, MediaType.Video, videoInfoOpt) =>
          videoInfoOpt.flatMap { videoInfo =>
            videoInfo.durationMills.flatMap { duration =>
              if (duration <= maxVideoDurationMs) Some(t)
              else None
            }
          }
          
        case _ => None
      }
    }
      
    case _ => None
  }
}
