package com.googlecast;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

public class GoogleCastRemoteMediaClientProgressListener implements RemoteMediaClient.ProgressListener {
    private GoogleCastModule module;

    public GoogleCastRemoteMediaClientProgressListener(GoogleCastModule module) {
        this.module = module;
    }

    @Override
    public void onProgressUpdated(final long progressMs, final long durationMs) {
        module.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
              MediaStatus mediaStatus = module.getCastSession().getRemoteMediaClient().getMediaStatus();
              
              if (mediaStatus == null) { return; }

              if (mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING) {
                module.emitMessageToRN(GoogleCastModule.MEDIA_PROGRESS_UPDATED, prepareProgressMessage(progressMs, durationMs));
              }
            }
        });
    }

    @NonNull
    private WritableMap prepareProgressMessage(long progressMs, long durationMs) {
        // needs to be constructed for every message from scratch because reusing a message fails with "Map already consumed"
        WritableMap map = Arguments.createMap();
        map.putInt("progress", (int) progressMs / 1000);
        map.putInt("duration", (int) durationMs / 1000);

        WritableMap message = Arguments.createMap();
        message.putMap("mediaProgress", map);
        return message;
    }
}
