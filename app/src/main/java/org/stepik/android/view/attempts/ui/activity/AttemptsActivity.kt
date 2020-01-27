package org.stepik.android.view.attempts.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_attempts.*
import kotlinx.android.synthetic.main.empty_default.*
import kotlinx.android.synthetic.main.progress_bar_on_empty_screen.*
import org.stepic.droid.R
import org.stepic.droid.base.App
import org.stepic.droid.base.FragmentActivityBase
import org.stepic.droid.ui.util.initCenteredToolbar
import org.stepic.droid.ui.util.setCompoundDrawables
import org.stepik.android.presentation.attempts.AttemptsPresenter
import org.stepik.android.presentation.attempts.AttemptsView
import org.stepik.android.view.attempts.model.AttemptCacheItem
import org.stepik.android.view.attempts.ui.adapter.delegate.AttemptLessonAdapterDelegate
import org.stepik.android.view.attempts.ui.adapter.delegate.AttemptSectionAdapterDelegate
import org.stepik.android.view.attempts.ui.adapter.delegate.AttemptSubmissionAdapterDelegate
import org.stepik.android.view.ui.delegate.ViewStateDelegate
import ru.nobird.android.ui.adapters.DefaultDelegateAdapter
import ru.nobird.android.ui.adapters.selection.MultipleChoiceSelectionHelper
import javax.inject.Inject

class AttemptsActivity : FragmentActivityBase(), AttemptsView {
    companion object {
        private const val EXTRA_COURSE_ID = "course_id"
        fun createIntent(context: Context, courseId: Long): Intent =
            Intent(context, AttemptsActivity::class.java)
                .putExtra(EXTRA_COURSE_ID, courseId)
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var attemptsPresenter: AttemptsPresenter

    private var attemptsAdapter: DefaultDelegateAdapter<AttemptCacheItem> = DefaultDelegateAdapter()
    private val selectionHelper = MultipleChoiceSelectionHelper(attemptsAdapter)

    private val viewStateDelegate =
        ViewStateDelegate<AttemptsView.State>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempts)

        injectComponent()
        attemptsPresenter = ViewModelProviders
            .of(this, viewModelFactory)
            .get(AttemptsPresenter::class.java)
        initCenteredToolbar(R.string.attempts_toolbar_title, showHomeButton = true)
        attemptsFeedback.setCompoundDrawables(start = R.drawable.ic_step_quiz_validation)

        attemptsAdapter += AttemptSectionAdapterDelegate(selectionHelper, onClick = ::handleSectionClick)
        attemptsAdapter += AttemptLessonAdapterDelegate(selectionHelper, onClick = ::handleLessonClick)
        attemptsAdapter += AttemptSubmissionAdapterDelegate(selectionHelper, onClick = ::handleSubmissionClick)

        with(attemptsRecycler) {
            itemAnimator = null
            adapter = attemptsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            addItemDecoration(DividerItemDecoration(this@AttemptsActivity, LinearLayoutManager.VERTICAL).apply {
                ContextCompat.getDrawable(this@AttemptsActivity, R.drawable.list_divider_h)?.let(::setDrawable)
            })
        }
        initViewStateDelegate()
        attemptsPresenter.fetchAttemptCacheItems()
    }

    private fun injectComponent() {
        App.component()
            .attemptsComponentBuilder()
            .build()
            .inject(this)
    }

    override fun onStart() {
        super.onStart()
        attemptsPresenter.attachView(this)
    }

    override fun onStop() {
        attemptsPresenter.detachView(this)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }

    private fun initViewStateDelegate() {
        viewStateDelegate.addState<AttemptsView.State.Idle>()
        viewStateDelegate.addState<AttemptsView.State.Loading>(loadProgressbarOnEmptyScreen)
        viewStateDelegate.addState<AttemptsView.State.Empty>(report_empty)
        viewStateDelegate.addState<AttemptsView.State.Error>()
        viewStateDelegate.addState<AttemptsView.State.AttemptsLoaded>(attemptsContainer, attemptsFeedback, attemptsFeedbackSeparator, attemptsRecycler)
    }

    override fun setState(state: AttemptsView.State) {
        viewStateDelegate.switchState(state)
        if (state is AttemptsView.State.AttemptsLoaded) {
            attemptsAdapter.items = state.attempts
        }
    }

    private fun isAllSubmissionsSelectedInLesson(lessonIndex: Int): Boolean {
        var areAllSelectedInLesson = true
        for (index in lessonIndex + 1 until attemptsAdapter.items.size) {
            val item = attemptsAdapter.items[index]
            if (item is AttemptCacheItem.SubmissionItem) {
                areAllSelectedInLesson = areAllSelectedInLesson && selectionHelper.isSelected(index)
            } else {
                break
            }
        }
        return areAllSelectedInLesson
    }

    private fun isAllLessonsSelectedInSection(sectionIndex: Int, sectionId: Long): Boolean {
        var areAllSelectedInSection = true
        for (index in sectionIndex + 1 until attemptsAdapter.items.size) {
            val item = attemptsAdapter.items[index]
            if (item is AttemptCacheItem.LessonItem) {
                if (item.section.id == sectionId) {
                    areAllSelectedInSection = areAllSelectedInSection && selectionHelper.isSelected(index)
                } else {
                    break
                }
            }
        }
        return areAllSelectedInSection
    }

    private fun handleSectionClick(attemptCacheSectionItem: AttemptCacheItem.SectionItem) {
        val itemIndex = attemptsAdapter.items.indexOf(attemptCacheSectionItem)
        val sectionId = attemptCacheSectionItem.section.id
        selectionHelper.toggle(itemIndex)
        val isSelected = selectionHelper.isSelected(itemIndex)

        for (index in itemIndex + 1 until attemptsAdapter.items.size) {
            val itemSectionId = when (val item = attemptsAdapter.items[index]) {
                is AttemptCacheItem.LessonItem ->
                    item.section.id
                is AttemptCacheItem.SubmissionItem ->
                    item.section.id
                else ->
                    -1L
            }

            if (itemSectionId == sectionId) {
                if (isSelected) {
                    selectionHelper.select(index)
                } else {
                    selectionHelper.deselect(index)
                }
            } else {
                break
            }
        }
    }

    private fun handleLessonClick(attemptCacheLessonItem: AttemptCacheItem.LessonItem) {
        val itemIndex = attemptsAdapter.items.indexOf(attemptCacheLessonItem)
        val lessonId = attemptCacheLessonItem.lesson.id
        val sectionId = attemptCacheLessonItem.section.id
        selectionHelper.toggle(itemIndex)
        val isSelected = selectionHelper.isSelected(itemIndex)

        for (index in itemIndex + 1 until attemptsAdapter.items.size) {
            val item = attemptsAdapter.items[index]
            if (item is AttemptCacheItem.SubmissionItem && item.lesson.id == lessonId) {
                if (isSelected) {
                    selectionHelper.select(index)
                } else {
                    selectionHelper.deselect(index)
                }
            } else {
                break
            }
        }

        val sectionIndex = attemptsAdapter.items.indexOfFirst { item ->
            item is AttemptCacheItem.SectionItem && item.section.id == sectionId
        }

        if (isAllLessonsSelectedInSection(sectionIndex, sectionId)) {
            selectionHelper.select(sectionIndex)
        } else {
            selectionHelper.deselect(sectionIndex)
        }
    }

    private fun handleSubmissionClick(attemptCacheSubmissionItem: AttemptCacheItem.SubmissionItem) {
        selectionHelper.toggle(attemptsAdapter.items.indexOf(attemptCacheSubmissionItem))
        val lessonId = attemptCacheSubmissionItem.lesson.id
        val sectionId = attemptCacheSubmissionItem.section.id

        val lessonIndex = attemptsAdapter.items.indexOfFirst { item ->
            item is AttemptCacheItem.LessonItem && item.lesson.id == lessonId
        }

        val sectionIndex = attemptsAdapter.items.indexOfFirst { item ->
            item is AttemptCacheItem.SectionItem && item.section.id == sectionId
        }

        if (isAllSubmissionsSelectedInLesson(lessonIndex)) {
            selectionHelper.select(lessonIndex)
        } else {
            selectionHelper.deselect(lessonIndex)
        }

        if (isAllLessonsSelectedInSection(sectionIndex, sectionId)) {
            selectionHelper.select(sectionIndex)
        } else {
            selectionHelper.deselect(sectionIndex)
        }
    }
}