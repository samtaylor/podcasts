package samtaylor.podcasts.playback

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class PlaybackService: Service(),
                       MediaPlayer.OnCompletionListener,
                       MediaPlayer.OnPreparedListener,
                       MediaPlayer.OnErrorListener,
                       MediaPlayer.OnSeekCompleteListener,
                       MediaPlayer.OnInfoListener,
                       MediaPlayer.OnBufferingUpdateListener,
                       AudioManager.OnAudioFocusChangeListener
{
    override fun onAudioFocusChange(focusChange: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPrepared(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val binder: IBinder = LocalBinder()

    override fun onBind( intent: Intent? ): IBinder
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class LocalBinder: Binder()
    {
        fun getService(): PlaybackService
        {
            return this@PlaybackService
        }
    }
}