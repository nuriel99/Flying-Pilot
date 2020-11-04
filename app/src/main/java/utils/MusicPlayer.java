package utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.konu.flyingpilot.R;

public class MusicPlayer {

    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 2;

    private static SoundPool soundPool;
    private static int hitSound;
    private static int coinSound;

    public MusicPlayer(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }


        coinSound = soundPool.load(context, R.raw.coincollect, 1);
        hitSound = soundPool.load(context, R.raw.hit, 1);
    }

    public void playCollectSound() {
        soundPool.play(coinSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playOverSound() {
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}