package com.muqingbfq;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import java.util.ArrayList;
import java.util.List;

public class PlaybackService extends MediaSessionService {
    public static MediaSession mediaSession = null;
    public static List<MediaSource> list = new ArrayList<>();

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
        player.setMediaSources(list);
//
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "26096272");
//        // 设置媒体源（音乐文件）
//        MediaItem mediaItem = MediaItem.fromUri(file.getPath()); // 替换为你的音乐文件路径或 URL
//        player.setMediaItem(mediaItem);
//
//        // 准备并开始播放
//        player.prepare();
//        player.play();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}