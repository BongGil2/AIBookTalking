package com.eduhansol.fmlibrary.utils;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.friendsmon.fmlibrary.tools.FMLog;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FMExoPlayer extends PlayerView {
    public enum PLAY_TYPE {
        HTTP, ASSETS
    }

    private PLAY_TYPE mPlayType = PLAY_TYPE.HTTP;

    private final Context mContext;
    private SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = true;


    private onFMExoPlayerListener mListener = null;

    public FMExoPlayer(Context context) {
        super(context);

        mContext = context;
        initPlayer();
    }

    public FMExoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initPlayer();
    }

    public FMExoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initPlayer();
    }

    private static void disableSSLCertificateVerify() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                        return myTrustedAnchors;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");

            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initPlayer() {
        exoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        exoPlayer.addListener(mPlayerListener);

        setPlayer(exoPlayer);
        //OS7 버전에서 Unity 뒤에서 Play되는 문제 수정
        if (getVideoSurfaceView() instanceof SurfaceView) {
            ((SurfaceView) getVideoSurfaceView()).setZOrderOnTop(true);
        }
    }

    private final Player.Listener mPlayerListener = new Player.Listener() {
        @Override
        public void onCues(List<Cue> cues) {

        }

        @Override
        public void onMetadata(Metadata metadata) {

        }

        @Override
        public void onPlaybackStateChanged(int state) {
            String stateString;
            switch (state) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE";
                    if (mListener != null) {
                        mListener.idle();
                    }
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING";
                    if (mListener != null) {
                        mListener.buffering();
                    }
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY";
                    if (mListener != null) {
                        mListener.ready();
                    }
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED";
                    if (mListener != null) {
                        mListener.end();
                    }
                    break;
                default:
                    stateString = "UNKNOWN_STATE";
                    break;
            }
            FMLog.d("changed state to " + stateString + ", playWhenReady: " + playWhenReady);
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            FMLog.d("changed reason to " + reason + ", playWhenReady: " + playWhenReady);
        }
    };

    public void playMedia(String url) {
        playMedia(url, true);
    }

    // 로딩이 완료 되면 바로 시작하지 않고 isPlayWhenReady 값에 따라 변경
    public void playMedia(String url, boolean isPlayWhenReady) {
        mPlayType = PLAY_TYPE.HTTP;
        playWhenReady = isPlayWhenReady;
        MediaSource mediaSource = url.endsWith(".m3u8") ? buildMediaSourceHLS(Uri.parse(url)) : buildMediaSource(Uri.parse(url));
        exoPlayer.setMediaSource(mediaSource, true);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(playWhenReady);
    }

    /**
     * assets에 있는 파일 재생
     *
     * @param path 폴더/파일명  or 파일명
     */
    public void playMediaAssets(String path, boolean isPlayWhenReady) {
        mPlayType = PLAY_TYPE.ASSETS;
        playWhenReady = isPlayWhenReady;
        exoPlayer.setMediaItem(MediaItem.fromUri("asset:///" + path));
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(playWhenReady);
    }

    public void stop() {
        playWhenReady = false;
        exoPlayer.stop();
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.getPlaybackState();
    }

    public void play() {
        playWhenReady = true;
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.getPlaybackState();
    }

    /**
     * 동영상 배속 증가
     *
     * @param speed 재생 배속
     */
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
        exoPlayer.setPlaybackParameters(playbackParameters);
    }

    private MediaSource buildMediaSource(Uri uri) {
        disableSSLCertificateVerify();

        String userAgent = System.getProperty("http.agent");
        FMLog.d("UserAgent : " + userAgent);
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(TextUtils.isEmpty(userAgent) ? "DefaultHttpDataSourceFactory" : userAgent);
        ProgressiveMediaSource videoSource =
                new ProgressiveMediaSource.Factory(manifestDataSourceFactory).createMediaSource(MediaItem.fromUri(uri));

        return new ConcatenatingMediaSource(videoSource);
    }

    private MediaSource buildMediaSourceHLS(Uri uri) {
        disableSSLCertificateVerify();

        String userAgent = System.getProperty("http.agent");
        FMLog.d("UserAgent : " + userAgent);
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent);
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(manifestDataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
        return new ConcatenatingMediaSource(hlsMediaSource);
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.removeListener(mPlayerListener);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void setVisibility(int gone) {
        super.setVisibility(gone);
    }

    public void setOnFMExoPlayerListener(onFMExoPlayerListener listener) {
        mListener = listener;
    }

    public interface onFMExoPlayerListener {
        void ready();

        void end();

        void buffering();

        void idle();
    }
}
