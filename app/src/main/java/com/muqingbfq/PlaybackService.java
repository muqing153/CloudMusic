package com.muqingbfq;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muqing.gj;
import com.muqingbfq.api.url;
import com.muqingbfq.fragment.Media;
import com.muqingbfq.mq.MediaItemAdapter;
import com.muqingbfq.mq.FilePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaybackService extends MediaSessionService {
    public static MediaSession mediaSession;
    public static List<MP3> list = new ArrayList<>();
    public static String lrc;
    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (mediaSession != null) {
                    mediaSession.getPlayer().pause(); // 暂停播放
                }
            }
        }
    };

    public static void ListSave() {
//        new Thread(() -> wj.xrwb(wj.filesdri + "list.json", new Gson().toJson(list))).start();
    }

    public Player.Listener PlayerListener = new Player.Listener() {
        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            // 处理 playWhenReady 的变化
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            // 处理播放状态的变化
            if (playbackState == Player.STATE_ENDED) {
                // 检查当前播放的媒体项是否是最后一项
                Player player = mediaSession.getPlayer();
                if (player.getCurrentMediaItemIndex() == player.getMediaItemCount() - 1) {
                    // 如果是最后一项，回到第一项并播放
                    player.seekTo(0, 0);
                    player.play();
                }
            } else if (playbackState == Player.EVENT_MEDIA_ITEM_TRANSITION) {
                MediaItem currentMediaItem1 = mediaSession.getPlayer().getCurrentMediaItem();
                new Thread(() -> {
                    if (currentMediaItem1 != null) {
                        url.getLrc(currentMediaItem1.mediaId);
//                        gj.sc("onPlaybackStateChanged:" + PlaybackService.lrc);
                        Media.loadLyric(PlaybackService.lrc);
                    }
                }).start();
                AddMediaItem(currentMediaItem1);

            }

        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
            // 处理位置不连续变化
        }

        private void AddMediaItem(MediaItem mediaItem) {
            new Thread(() -> {
//                gj.sc("onPlaybackStateChanged");
                try {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(MediaItem.class, new MediaItemAdapter()) // 绑定适配器
                            .create();
                    String dqwb = FilePath.dqwb(FilePath.gd + "mp3_listHistory.json");
                    List<MediaItem> listHistory;
                    if (Strings.isNullOrEmpty(dqwb)) {
                        listHistory = new ArrayList<>();
                    } else {
                        listHistory = gson.fromJson(dqwb, MediaItemAdapter.type);
                    }
                    listHistory.removeIf(mediaItem1 -> mediaItem1.mediaId.equals(mediaItem.mediaId));
                    listHistory.add(0, mediaItem);
                    String json = gson.toJson(listHistory);
                    FilePath.xrwb(FilePath.gd + "mp3_listHistory.json", json);
                } catch (Exception e) {
                    FilePath.sc(FilePath.gd + "mp3_listHistory.json");
                    gj.sc(e);
                }
            }).start();
        }

        /**
         * 切换歌曲
         */
        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, @Player.MediaItemTransitionReason int reason) {
        }

        @Override
        public void onTracksChanged(@Nullable Tracks tracks) {
            gj.sc(tracks);
            // Update UI using current tracks.
        }

        int error_count = 0;
        int currentIndex = 0;
        MediaItem currentMediaItem = null;

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player player = mediaSession.getPlayer();
            boolean shuffleModeEnabled = player.getShuffleModeEnabled();
            if (shuffleModeEnabled) {
                player.setShuffleModeEnabled(false);
            }

            if (++error_count > 3) {
                int nextIndex = player.getNextMediaItemIndex();
                if (nextIndex != C.INDEX_UNSET) {
                    gj.sc("播放失败，已跳过");
                    player.seekToDefaultPosition(nextIndex);
                    player.prepare();
                    player.play();
                } else {
                    gj.sc("播放失败，且无可跳过曲目");
                }
                error_count = 0;
                return;
            }

            // 尝试修复当前项
            currentIndex = player.getCurrentMediaItemIndex();
            currentMediaItem = player.getCurrentMediaItem();
            if (currentMediaItem == null || currentIndex == C.INDEX_UNSET) {
                gj.sc("当前播放项无效，无法重试");
                return;
            }
            new Thread(() -> {
                MP3 hq = url.hq(new MP3(currentMediaItem.mediaId));
                if (hq == null) {
                    return;
                }
                hq.picurl = url.picurl(hq.id);

                MediaItem newMediaItem = currentMediaItem.buildUpon()
                        .setUri(hq.url)
                        .build();

                main.handler.post(() -> {
                    player.replaceMediaItem(currentIndex, newMediaItem);
                    player.prepare();
                    player.play();
                    error_count = 0;
                    player.setShuffleModeEnabled(shuffleModeEnabled);
                });
            }).start();
        }

    };


    @UnstableApi
    public void onCreate() {
        super.onCreate();
        if (PlaybackService.mediaSession == null) {
            PlaybackService.mediaSession = new MediaSession.Builder(this, new ExoPlayer.Builder(this)
                    .build()).build();
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(this, home.class));//用ComponentName得到class对象
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mediaSession.setSessionActivity(pendingIntent);
// 添加 ExoPlayer 监听器

//
//        String nickname = wj.dqwb(wj.filesdri + "list.json");
//        if (!Strings.isNullOrEmpty(nickname)) {
//            try {
//                list = new Gson().fromJson(nickname, new com.google.gson.reflect.TypeToken<List<MP3>>() {
//                }.getType());
//                for (MP3 mediaItem : list) {
//                    player.addMediaItem(PlaybackService.GetMp3(mediaItem));
//                }
//            } catch (Exception e) {
//                gj.sc(e);
//                list = new ArrayList<>();
//                wj.sc(wj.filesdri + "list.json");
//            }
//        }

        String mp3_listHistory = FilePath.dqwb(FilePath.gd + "mp3_listHistory.json");
        if (!Strings.isNullOrEmpty(mp3_listHistory)) {
            try {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(MediaItem.class, new MediaItemAdapter()) // 绑定适配器
                        .create();
//                序列化 List<MediaItem>（转换成 JSON）
//                String json = gson.toJson(listHistory);
//                System.out.println("JSON 数据: " + json);
//                反序列化 JSON（恢复 List<MediaItem>）
                List<MediaItem> list = gson.fromJson(mp3_listHistory, MediaItemAdapter.type);
                mediaSession.getPlayer().addMediaItems(list);
//                gj.sc("listHistory:" + listHistory.size());
            } catch (Exception e) {
                gj.sc("listHistory:" + e);
            }
        }

        mediaSession.getPlayer().addListener(PlayerListener);

        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, filter);
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
        unregisterReceiver(noisyReceiver);
        super.onDestroy();
    }

    public static MediaItem GetMp3(MP3 mp3) {
        // 创建媒体的元数据（如标题、描述、图片等）
        MediaMetadata.Builder builder = new MediaMetadata.Builder()
                .setTitle(mp3.name)
                .setArtist(mp3.zz)
                .setAlbumTitle(mp3.zz);
        if (mp3.picurl != null) {
            builder.setArtworkUri(Uri.parse(mp3.picurl));
        } else if (mp3.picdata != null) {
            builder.maybeSetArtworkData(mp3.picdata, mp3.picdata.length);
        }
        MediaMetadata mediaMetadata = builder.build();
// 创建带有元数据的 MediaItem
        MediaItem.Builder metadata = new MediaItem.Builder()
                .setMediaId(mp3.id) // 设置媒体的唯一ID
                .setUri(Strings.isNullOrEmpty(mp3.url) ? "" : mp3.url)
                .setMediaMetadata(mediaMetadata);
//        if (mp3.picurl != null) {
//
//        }else if (mp3.picdata)

        return metadata.build();
    }


    public static void AddMediaItem(MP3 mp3) {
        if (mediaSession != null) {
            new Thread(() -> {
                MediaItem mediaItem = GetMp3(mp3);
                main.handler.post(() -> {
                    Player player = mediaSession.getPlayer();
                    for (int i = 0; i < player.getMediaItemCount(); i++) {
                        MediaItem existingItem = player.getMediaItemAt(i);
                        if (Objects.equals(existingItem.mediaId, mp3.id)) {
                            player.seekTo(i, 0);
                            return;
                        }
                    }
                    player.addMediaItem(0, mediaItem);
                    player.seekTo(0, 0);
                    player.prepare();
                    player.play();
                });
            }).start();
        }
    }
}