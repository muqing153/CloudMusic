package com.muqingbfq;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
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
import com.google.gson.reflect.TypeToken;
import com.muqingbfq.api.url;
import com.muqingbfq.mq.MediaItemAdapter;
import com.muqingbfq.mq.gj;
import com.muqingbfq.mq.wj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaybackService extends MediaSessionService {
    public static MediaSession mediaSession = null;
    public static List<MP3> list = new ArrayList<>();


    public static void ListSave() {
        new Thread(() -> wj.xrwb(wj.filesdri + "list.json", new Gson().toJson(list))).start();
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
            }

        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
            // 处理位置不连续变化
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, @Player.MediaItemTransitionReason int reason) {
            if (mediaItem != null) {
                // 输出当前的 MediaItem 信息
                String title = mediaItem.mediaMetadata.title != null ? mediaItem.mediaMetadata.title.toString() : "未知标题";
                String artist = mediaItem.mediaMetadata.artist != null ? mediaItem.mediaMetadata.artist.toString() : "未知艺术家";
//                gj.sc(title + " - " + artist);
                new Thread(() -> {
                    try {
                        String dqwb = wj.dqwb(wj.gd + "mp3_listHistory.json");
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(MediaItem.class, new MediaItemAdapter()) // 绑定适配器
                                .create();
                        List<MediaItem> listHistory = gson.fromJson(dqwb, new TypeToken<List<MediaItem>>(){}.getType());
                        if (listHistory != null) {
                            listHistory.removeIf(mediaItem1 -> mediaItem1.mediaId.equals(mediaItem.mediaId));
                            listHistory.add(0, mediaItem);
                        }
                        String json = gson.toJson(listHistory);
                        wj.xrwb(wj.gd + "mp3_listHistory.json", json);
                    } catch (Exception e) {
                        gj.sc(e);
                    }
                }).start();
//                bfqkz.lishi_list.removeIf(mp3 -> mp3.id.equals(mediaItem.mediaId));
//                bfqkz.lishi_list.add(0, new MP3(mediaItem.mediaId, title, artist, mediaItem.mediaMetadata.artworkUri.toString()));
//                new Thread(() -> wj.xrwb(wj.gd + "mp3_hc.json", new Gson().toJson(bfqkz.lishi_list))).start();
            }
        }

        @Override
        public void onTracksChanged(@Nullable Tracks tracks) {
            gj.sc(tracks);
            // Update UI using current tracks.
        }

        int error_count = 0;//设置错误次数
        MediaItem currentMediaItem = null;
        int currentIndex = 0;

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            // 当播放发生错误时调用
            // 如果错误是由于资源找不到
            Player player = mediaSession.getPlayer();
            if (++error_count > 3) {
                currentIndex = player.getNextMediaItemIndex();
                if (currentIndex != C.INDEX_UNSET) {
                    gj.sc("播放失败，已跳过");
                    return;
                } else {
                    currentMediaItem = player.getMediaItemAt(currentIndex);  // 获取下一首的 MediaItem
                }
            } else {
                currentMediaItem = player.getCurrentMediaItem();
                currentIndex = player.getCurrentMediaItemIndex();  // 获取当前播放项的索引
            }
            new Thread(() -> {
                MP3 hq = url.hq(new MP3(currentMediaItem.mediaId));
                hq.picurl = url.picurl(hq.id);
                // 设置新的 MediaItem
                MediaItem newMediaItem = currentMediaItem.buildUpon()
                        .setUri(hq.url)  // 更新 URI
                        .build();  // 构建新的 MediaItem
                main.handler.post(() -> {
                    player.replaceMediaItem(currentIndex, newMediaItem);
                    player.prepare();
                    player.play();
                    error_count = 0;
                });
            }).start();
        }
    };


    @UnstableApi
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this)
                .build();
        mediaSession = new MediaSession.Builder(this, player).build();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(this, home.class));//用ComponentName得到class对象
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mediaSession.setSessionActivity(pendingIntent);
// 添加 ExoPlayer 监听器


        String nickname = wj.dqwb(wj.filesdri + "list.json");
        if (!Strings.isNullOrEmpty(nickname)) {
            try {
                list = new Gson().fromJson(nickname, new com.google.gson.reflect.TypeToken<List<MP3>>() {
                }.getType());
                for (MP3 mediaItem : list) {
                    player.addMediaItem(PlaybackService.GetMp3(mediaItem));
                }
            } catch (Exception e) {
                gj.sc(e);
                list = new ArrayList<>();
                wj.sc(wj.filesdri + "list.json");
            }
        }

//        String mp3_listHistory = wj.dqwb(wj.gd + "mp3_listHistory.json");
//        if (!Strings.isNullOrEmpty(mp3_listHistory)) {
//            try {
//                Gson gson = new GsonBuilder()
//                        .registerTypeAdapter(MediaItem.class, new MediaItemAdapter()) // 绑定适配器
//                        .create();
////                序列化 List<MediaItem>（转换成 JSON）
////                String json = gson.toJson(listHistory);
////                System.out.println("JSON 数据: " + json);
////                反序列化 JSON（恢复 List<MediaItem>）
//                listHistory.addAll(gson.fromJson(mp3_listHistory, new com.google.gson.reflect.TypeToken<List<MediaItem>>() {
//                }.getType()));
//                gj.sc("listHistory:" + listHistory.size());
//            } catch (Exception e) {
//                gj.sc("listHistory:" + e);
//            }
//        }

        player.addListener(PlayerListener);
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

    public static MediaItem GetMp3(MP3 mp3) {
        // 创建媒体的元数据（如标题、描述、图片等）
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(mp3.name)
                .setArtist(mp3.zz)
                .setAlbumTitle(mp3.zz)
                .setArtworkUri(Uri.parse(mp3.picurl)) // 图片URL
                .build();
// 创建带有元数据的 MediaItem
        return new MediaItem.Builder()
                .setMediaId(mp3.id) // 设置媒体的唯一ID
                .setUri(Strings.isNullOrEmpty(mp3.url) ? "" : mp3.url)
                .setMediaMetadata(mediaMetadata) // 将元数据添加到 MediaItem
                .build();
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