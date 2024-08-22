package com.muqingbfq.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;

import com.muqingbfq.PlaybackService;
import com.muqingbfq.R;
import com.muqingbfq.databinding.ActivityMusicBinding;
import com.muqingbfq.mq.AppCompatActivity;

public class Music extends AppCompatActivity<ActivityMusicBinding> {

    private Player player = PlaybackService.mediaSession.getPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
        binding.kg.setImageResource(shouldShowPlayButton ? R.drawable.zt : R.drawable.bf);
        binding.kg.setOnClickListener(view -> Util.handlePlayPauseButtonAction(player));
        player.addListener(new Player.Listener() {
            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                if (events.containsAny(
                        Player.EVENT_PLAY_WHEN_READY_CHANGED,
                        Player.EVENT_PLAYBACK_STATE_CHANGED,
                        Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED)) {
                    updatePlayPauseButton();
                }
                if (events.containsAny(Player.EVENT_REPEAT_MODE_CHANGED)) {
                    updateRepeatModeButton();
                }
            }
        });
    }

    private void updatePlayPauseButton(){
        boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
        binding.kg.setImageResource(shouldShowPlayButton ? R.drawable.zt : R.drawable.bf);
    }

    private void updateRepeatModeButton() {
        boolean shouldShowPlayButton = Util.shouldShowPlayButton(player);
        binding.kg.setImageResource(shouldShowPlayButton ? R.drawable.zt : R.drawable.bf);
    }

    @Override
    protected ActivityMusicBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityMusicBinding.inflate(layoutInflater);
    }
}
