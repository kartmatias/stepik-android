package org.stepic.droid.mappers

import org.stepic.droid.model.DbVideoUrl
import org.stepic.droid.model.VideoUrl

fun VideoUrl.toDbUrl(videoId: Long) =
        DbVideoUrl(
                videoId = videoId,
                url = this.url,
                quality = this.quality
        )

fun DbVideoUrl.toVideoUrl() =
        VideoUrl(
                this.url,
                this.quality
        )

fun List<DbVideoUrl>.toVideoUrls(): List<VideoUrl> {
    val result = ArrayList<VideoUrl>()
    this.forEach {
        result.add(it.toVideoUrl())
    }
    return result
}