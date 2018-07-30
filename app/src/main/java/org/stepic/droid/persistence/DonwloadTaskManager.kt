package org.stepic.droid.persistence

import io.reactivex.Completable
import org.stepic.droid.persistence.model.DownloadConfiguration
import org.stepic.droid.persistence.model.PersistentItem

interface DonwloadTaskManager {
    fun addTask(persistentItem: PersistentItem, configuration: DownloadConfiguration): Completable

    fun updateTask(persistentItem: PersistentItem): Completable
    fun removeTask(persistentItem: PersistentItem): Completable
}