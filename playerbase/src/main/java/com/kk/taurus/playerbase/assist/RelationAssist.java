package com.kk.taurus.playerbase.assist;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.kk.taurus.playerbase.AVPlayer;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.provider.IDataProvider;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.render.IRender;
import com.kk.taurus.playerbase.render.RenderTextureView;
import com.kk.taurus.playerbase.widget.ViewContainer;

/**
 *
 * This class is mainly used for association between player and component view.
 * For example, maybe you need to switch playback in different views.
 * In this scene, you can use this class.
 * You only need to import your layout container and playback resources.
 *
 */
public class RelationAssist implements AssistPlay {

    private Context mContext;

    private AVPlayer mPlayer;
    private ViewContainer mViewContainer;
    private ReceiverGroup mReceiverGroup;

    private IRender mRender;
    private IRender.IRenderHolder mRenderHolder;

    private DataSource mDataSource;

    private int mVideoRotation;
    private int mVideoWidth,mVideoHeight;
    private int mVideoSarNum,mVideoSarDen;

    private OnPlayerEventListener mOnPlayerEventListener;
    private OnErrorEventListener mOnErrorEventListener;
    private OnReceiverEventListener mOnReceiverEventListener;

    private OnEventAssistHandler mOnEventAssistHandler;

    public RelationAssist(Context context){
        this.mContext = context;
        mPlayer = new AVPlayer();
        mViewContainer = new ViewContainer(context);
    }

    public ViewContainer getViewContainer() {
        return mViewContainer;
    }

    private void attachPlayerListener(){
        mPlayer.setOnPlayerEventListener(mInternalPlayerEventListener);
        mPlayer.setOnErrorEventListener(mInternalErrorEventListener);
        mViewContainer.setOnReceiverEventListener(mInternalReceiverEventListener);
    }

    private void detachPlayerListener(){
        mPlayer.setOnPlayerEventListener(null);
        mPlayer.setOnErrorEventListener(null);
        mViewContainer.setOnReceiverEventListener(null);
    }

    private OnPlayerEventListener mInternalPlayerEventListener =
            new OnPlayerEventListener() {
        @Override
        public void onPlayerEvent(int eventCode, Bundle bundle) {
            onInternalHandlePlayerEvent(eventCode, bundle);
            mViewContainer.dispatchPlayEvent(eventCode, bundle);
            if(mOnPlayerEventListener!=null)
                mOnPlayerEventListener.onPlayerEvent(eventCode, bundle);
        }
    };

    private void onInternalHandlePlayerEvent(int eventCode, Bundle bundle) {
        if(eventCode== OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE){
            mVideoWidth = bundle.getInt(EventKey.INT_ARG1);
            mVideoHeight = bundle.getInt(EventKey.INT_ARG2);
            mVideoSarNum = bundle.getInt(EventKey.INT_ARG3);
            mVideoSarDen = bundle.getInt(EventKey.INT_ARG4);
            if(mRender!=null){
                mRender.updateVideoSize(mVideoWidth, mVideoHeight);
                mRender.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
            }
        }else if(eventCode== OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_ROTATION_CHANGED){
            mVideoRotation = bundle.getInt(EventKey.INT_DATA);
            if(mRender!=null)
                mRender.setVideoRotation(mVideoRotation);
        }else if(eventCode== OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED){
            bindRenderHolder(mRenderHolder);
        }
    }

    private OnErrorEventListener mInternalErrorEventListener =
            new OnErrorEventListener() {
        @Override
        public void onErrorEvent(int eventCode, Bundle bundle) {
            onInternalHandleErrorEvent(eventCode, bundle);
            mViewContainer.dispatchErrorEvent(eventCode, bundle);
            if(mOnErrorEventListener!=null)
                mOnErrorEventListener.onErrorEvent(eventCode, bundle);
        }
    };

    private void onInternalHandleErrorEvent(int eventCode, Bundle bundle) {
        //not handle
    }

    public void setEventAssistHandler(
            OnEventAssistHandler onEventAssistHandler) {
        this.mOnEventAssistHandler = onEventAssistHandler;
    }

    private OnReceiverEventListener mInternalReceiverEventListener =
            new OnReceiverEventListener() {
        @Override
        public void onReceiverEvent(int eventCode, Bundle bundle) {
            if(mOnEventAssistHandler !=null)
                mOnEventAssistHandler.onAssistHandle(RelationAssist.this, eventCode, bundle);
            if(mOnReceiverEventListener!=null)
                mOnReceiverEventListener.onReceiverEvent(eventCode, bundle);
        }
    };

    @Override
    public void setOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        this.mOnPlayerEventListener = onPlayerEventListener;
    }

    @Override
    public void setOnErrorEventListener(OnErrorEventListener onErrorEventListener) {
        this.mOnErrorEventListener = onErrorEventListener;
    }

    @Override
    public void setOnReceiverEventListener(OnReceiverEventListener onReceiverEventListener) {
        this.mOnReceiverEventListener = onReceiverEventListener;
    }

    @Override
    public void setOnProviderListener(IDataProvider.OnProviderListener onProviderListener) {
        mPlayer.setOnProviderListener(onProviderListener);
    }

    @Override
    public void setDataProvider(IDataProvider dataProvider) {
        mPlayer.setDataProvider(dataProvider);
    }

    @Override
    public void setReceiverGroup(ReceiverGroup receiverGroup) {
        this.mReceiverGroup = receiverGroup;
    }

    public ReceiverGroup getReceiverGroup() {
        return mReceiverGroup;
    }

    @Override
    public void attachContainer(ViewGroup userContainer) {
        mPlayer.setSurface(null);
        attachPlayerListener();
        detachViewContainer();
        if(mReceiverGroup!=null){
            mViewContainer.setReceiverGroup(mReceiverGroup);
        }
        releaseRender();
        mRender = new RenderTextureView(mContext);
        mRender.setRenderCallback(mRenderCallback);
        updateRenderParams();
        mViewContainer.setRenderView(mRender.getRenderView());
        if(userContainer!=null){
            userContainer.addView(mViewContainer,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void setDataSource(DataSource dataSource){
        this.mDataSource = dataSource;
    }

    @Override
    public void play() {
        if(mDataSource!=null){
            onInternalSetDataSource(mDataSource);
            onInternalStart(mDataSource.getStartPos());
        }
    }

    private IRender.IRenderCallback mRenderCallback =
            new IRender.IRenderCallback() {
        @Override
        public void onSurfaceCreated(IRender.IRenderHolder renderHolder,
                                     int width, int height) {
            mRenderHolder = renderHolder;
            bindRenderHolder(mRenderHolder);
        }
        @Override
        public void onSurfaceChanged(IRender.IRenderHolder renderHolder,
                                     int format, int width, int height) {

        }
        @Override
        public void onSurfaceDestroy(IRender.IRenderHolder renderHolder) {
            mRenderHolder = null;
        }
    };

    private void bindRenderHolder(IRender.IRenderHolder renderHolder){
        if(renderHolder!=null)
            renderHolder.bindPlayer(mPlayer);
    }

    private void updateRenderParams(){
        if(mRender!=null){
            //if render change ,need update some params
            mRender.updateVideoSize(mVideoWidth, mVideoHeight);
            mRender.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
            mRender.setVideoRotation(mVideoRotation);
        }
    }

    private void releaseRender(){
        if(mRender!=null)
            mRender.release();
    }

    private void detachViewContainer(){
        ViewParent parent = mViewContainer.getParent();
        if(parent!=null && parent instanceof ViewGroup){
            ((ViewGroup) parent).removeView(mViewContainer);
        }
    }

    private void onInternalSetDataSource(DataSource dataSource){
        mPlayer.setDataSource(dataSource);
    }

    private void onInternalStart(int msc){
        mPlayer.start(msc);
    }

    @Override
    public boolean isInPlaybackState() {
        int state = getState();
        return state!= IPlayer.STATE_END
                && state!= IPlayer.STATE_ERROR
                && state!= IPlayer.STATE_IDLE
                && state!= IPlayer.STATE_INITIALIZED
                && state!= IPlayer.STATE_STOPPED;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    @Override
    public int getState() {
        return mPlayer.getState();
    }

    @Override
    public void rePlay(int msc) {
        if(mDataSource!=null){
            onInternalSetDataSource(mDataSource);
            onInternalStart(msc);
        }
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void resume() {
        mPlayer.resume();
    }

    @Override
    public void seekTo(int msc) {
        mPlayer.seekTo(msc);
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void reset() {
        mPlayer.reset();
    }

    @Override
    public void destroy() {
        detachPlayerListener();
        detachViewContainer();
        releaseRender();
        setReceiverGroup(null);
        mViewContainer.destroy();
        mPlayer.destroy();
    }
}
