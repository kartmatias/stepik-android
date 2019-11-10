package org.stepik.android.remote.unit.service

import io.reactivex.Single
import org.stepik.android.remote.unit.model.UnitResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UnitService {
    @GET("api/units")
    fun getUnitsRx(@Query("ids[]") unitIds: LongArray): Single<UnitResponse>

    @GET("api/units")
    fun getUnitsByLessonId(@Query("lesson") lessonId: Long): Single<UnitResponse>
}