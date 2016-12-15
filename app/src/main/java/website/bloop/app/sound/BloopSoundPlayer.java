package website.bloop.app.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import website.bloop.app.R;

/**
 * Play sounds for bloops and capturing other player's flags.
 */
public class BloopSoundPlayer {
    private final Context mContext;

    private SoundPool mSoundPool;
    private final int mBoop;
    private final int mBloop;

    private static final int MAX_AUDIO_STREAMS = 12;
    private static final float VOLUME = 1f;
    private static final int PRIORITY = 1;
    private static final int REPEATS = 0;
    private static final float PLAYBACK_SPEED = 1f;

    public BloopSoundPlayer(Context context) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes soundAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_AUDIO_STREAMS)
                    .setAudioAttributes(soundAttributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_AUDIO_STREAMS, AudioManager.STREAM_MUSIC, 100);
        }

        mBoop = mSoundPool.load(mContext, R.raw.boop, 1);
        mBloop = mSoundPool.load(mContext, R.raw.bloop, 1);
    }

    /**
     * Shorter noise that happens when the bloops are drawn on screen.
     */
    public void boop() {
        mSoundPool.play(mBoop, VOLUME, VOLUME, PRIORITY, REPEATS, PLAYBACK_SPEED);
    }

    /**
     * Sound which happens when a user captures another's flag.
     */
    public void bloop() {
        mSoundPool.play(mBloop, VOLUME, VOLUME, PRIORITY, REPEATS, PLAYBACK_SPEED);
    }
}
