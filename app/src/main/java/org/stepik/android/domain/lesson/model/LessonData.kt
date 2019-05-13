package org.stepik.android.domain.lesson.model

import org.stepik.android.model.Lesson
import org.stepik.android.model.Section
import org.stepik.android.model.Unit

data class LessonData(
    val lesson: Lesson,
    val unit: Unit,
    val section: Section,
    val stepPosition: Long = 1
)