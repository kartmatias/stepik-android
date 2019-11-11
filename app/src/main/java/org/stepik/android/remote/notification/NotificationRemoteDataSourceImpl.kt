package org.stepik.android.remote.notification

import io.reactivex.Completable
import io.reactivex.Single
import org.stepic.droid.model.NotificationCategory
import org.stepic.droid.notifications.model.Notification
import org.stepic.droid.web.NotificationRequest
import org.stepic.droid.web.NotificationResponse
import org.stepic.droid.web.NotificationStatusesResponse
import org.stepik.android.data.notification.source.NotificationRemoteDataSource
import org.stepik.android.remote.notification.service.NotificationService
import javax.inject.Inject

class NotificationRemoteDataSourceImpl
@Inject
constructor(
    private val notificationService: NotificationService
) : NotificationRemoteDataSource {

    override fun putNotifications(vararg notificationIds: Long, isRead: Boolean): Completable =
        Completable.concat(notificationIds.map { id ->
            val notification = Notification()
            notification.isUnread = !isRead
            notificationService.putNotificationReactive(id, NotificationRequest(notification))
        })

    override fun getNotificationsResponse(notificationCategory: NotificationCategory, page: Int): Single<NotificationResponse> {
        val category = getNotificationCategoryString(notificationCategory)
        return notificationService.getNotifications(page, category)
    }

    override fun markNotificationAsRead(notificationCategory: NotificationCategory): Completable {
        val category = getNotificationCategoryString(notificationCategory)
        return notificationService.markNotificationAsRead(category)
    }

    override fun getNotificationStatuses(): Single<NotificationStatusesResponse> =
        notificationService.getNotificationStatuses()

    private fun getNotificationCategoryString(notificationCategory: NotificationCategory): String? {
        return if (notificationCategory === NotificationCategory.all) {
            null
        } else {
            notificationCategory.name
        }
    }
}

