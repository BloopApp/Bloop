package website.bloop.app;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class BloopSoundPlayer {
    private final Context mContext;

    private SoundPool mSoundPool;
    private final int mBoop;
    private final int mBloop;

    private static final int MAX_AUDIO_STREAMS = 6;
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

    public void boop() {
        mSoundPool.play(mBoop, VOLUME, VOLUME, PRIORITY, REPEATS, PLAYBACK_SPEED);
    }

    public void bloop() {
        mSoundPool.play(mBloop, VOLUME, VOLUME, PRIORITY, REPEATS, PLAYBACK_SPEED);
    }
}
