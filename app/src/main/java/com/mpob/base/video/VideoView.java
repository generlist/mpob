package com.mpob.base.video;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.util.Util;
import com.mpob.base.R;
import com.mpob.base.pojos.Camera;

import java.util.List;


/**
 * Created by HOLV on 3,December,2017
 * My Parents On Board,
 * Santa Monica California.
 */

public class VideoView extends AppCompatActivity
        implements IVideoAPI.View {

    private static final String TAG = "VideoView";
    private IVideoAPI.Presenter mIPresenter = null;
    private ProgressBar mProgressBar = null;
    private GestureDetectorCompat mDetector = null;
    private IVideoAPI.VideoPlayerGestureDetector mGestureDetectorListener = null;
    private View thisView = null;
    private RecyclerView mRecyclerView = null;
    private VideoAdapter mVideoAdapter = null;
    private boolean mIsPlaying = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_video_recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_video_progress);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        //makes transparent the status and navigation bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mIPresenter = new VideoPresenter(this);
        mIPresenter.init();

        thisView = this.getWindow().getDecorView().findViewById(R.id.activity_video_framelayout);
        thisView.requestFocus();

        mGestureDetectorListener = new VideoGestureDetector(this);
        mDetector = new GestureDetectorCompat(this, mGestureDetectorListener);
        // we will use gestures where easy to hint player actions
        thisView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                // Be sure to call the superclass implementation
                return true;
            }
        });

    }

    // This callback is called only when there is a saved instance previously saved using
    // onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mIsPlaying = savedInstanceState.getBoolean("isPlaying");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isPlaying", mIsPlaying);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        //set player to height to 230dp and width to match_parent
        Log.d(TAG, "holv :"+(short) newConfig.orientation);
        mIPresenter.configChanged((short) newConfig.orientation);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void loadRecyclerView(List<Camera> list) {
        mVideoAdapter = new VideoAdapter(getApplicationContext(), list);
        mIPresenter.setAdapterCallBack(mVideoAdapter);
        mRecyclerView.setAdapter(mVideoAdapter);
    }


    @Override
    public void onPause() {
        if (Util.SDK_INT <= 23) {
            mIsPlaying = true;
            mIPresenter.releasePlayerResources();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) {
            mIsPlaying = true;
            mIPresenter.releasePlayerResources();
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (mIsPlaying) {
                mIPresenter.init();
                mIPresenter.play();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23)) {
            if (mIsPlaying) {
                mIPresenter.init();
                mIPresenter.play();
            }
        }
    }


}
