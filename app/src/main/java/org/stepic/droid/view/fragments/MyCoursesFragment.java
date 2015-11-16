package org.stepic.droid.view.fragments;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import org.stepic.droid.base.CoursesFragmentBase;
import org.stepic.droid.events.courses.FailDropCourseEvent;
import org.stepic.droid.events.courses.SuccessDropCourseEvent;
import org.stepic.droid.events.notify_ui.NotifyUICoursesEvent;
import org.stepic.droid.events.courses.FailCoursesDownloadEvent;
import org.stepic.droid.events.courses.FinishingGetCoursesFromDbEvent;
import org.stepic.droid.events.courses.FinishingSaveCoursesToDbEvent;
import org.stepic.droid.events.courses.GettingCoursesFromDbSuccessEvent;
import org.stepic.droid.events.courses.PreLoadCoursesEvent;
import org.stepic.droid.events.courses.StartingGetCoursesFromDbEvent;
import org.stepic.droid.events.courses.StartingSaveCoursesToDbEvent;
import org.stepic.droid.events.courses.SuccessCoursesDownloadEvent;
import org.stepic.droid.store.operations.DatabaseManager;

public class MyCoursesFragment extends CoursesFragmentBase {


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected DatabaseManager.Table getCourseType() {
        return DatabaseManager.Table.enrolled;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    @Subscribe
    public void onFailureDataLoad(FailCoursesDownloadEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onFailureDataLoad(e);
    }

    @Override
    @Subscribe
    public void onStartingSaveToDb(StartingSaveCoursesToDbEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onStartingSaveToDb(e);
    }

    @Override
    @Subscribe
    public void onFinishingSaveToDb(FinishingSaveCoursesToDbEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onFinishingSaveToDb(e);
    }

    @Override
    @Subscribe
    public void onStartingGetFromDb(StartingGetCoursesFromDbEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onStartingGetFromDb(e);
    }

    @Override
    @Subscribe
    public void onFinishingGetFromDb(FinishingGetCoursesFromDbEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onFinishingGetFromDb(e);
    }

    @Subscribe
    public void onGettingFromDbSuccess(GettingCoursesFromDbSuccessEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onGettingFromDbSuccess(e);
    }

    @Subscribe
    @Override
    public void onSuccessDataLoad(SuccessCoursesDownloadEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onSuccessDataLoad(e);
    }

    @Subscribe
    @Override
    public void onPreLoad(PreLoadCoursesEvent e) {
        if (e.getType() == DatabaseManager.Table.enrolled)
            super.onPreLoad(e);
    }

    @Subscribe
    @Override
    public void onNotifyUI(NotifyUICoursesEvent e) {
        super.onNotifyUI(e);
    }


    @Subscribe
    @Override
    public void onSuccessDrop(SuccessDropCourseEvent e) {
        super.onSuccessDrop(e);
    }

    @Subscribe
    @Override
    public void onFailDrop(FailDropCourseEvent e) {
        super.onFailDrop(e);
    }
}