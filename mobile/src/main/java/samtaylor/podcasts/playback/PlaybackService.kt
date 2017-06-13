package samtaylor.podcasts.playback

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class PlaybackService: Service(),
                       MediaPlayer.OnCompletionListener,
                       MediaPlayer.OnPreparedListener,
                       MediaPlayer.OnErrorListener,
                       MediaPlayer.OnSeekCompleteListener,
                       MediaPlayer.OnInfoListener,
                       MediaPlayer.OnBufferingUpdateListener,
                       AudioManager.OnAudioFocusChangeListener
{
    private var mediaPlayer: MediaPlayer? = null

    private var mediaFile: String? = null

    private var resumePosition = 0

    private var audioManager: AudioManager? = null

    override fun onStartCommand( intent: Intent?, flags: Int, startId: Int ): Int
    {
        this.mediaFile = intent?.extras?.get( "media" ) as String

        if ( this.mediaFile != null && !this.mediaFile.equals( "" ) )
        {
            if ( !this.requestAudioFocus() )
            {
                this.stopSelf()
            }

            this.initMediaPlayer()
        }
        else
        {
            this.stopSelf()
        }

        return super.onStartCommand( intent, flags, startId )
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.mediaPlayer?.let {
            this.stopMedia()
            it.release()
        }

        this.removeAudioFocus()
    }

    private fun initMediaPlayer()
    {
        this.mediaPlayer = MediaPlayer()

        this.mediaPlayer?.let {
            it.setOnCompletionListener( this )
            it.setOnPreparedListener( this )
            it.setOnErrorListener( this )
            it.setOnSeekCompleteListener ( this )
            it.setOnInfoListener( this )
            it.setOnBufferingUpdateListener( this )

            it.reset()

            it.setAudioStreamType( AudioManager.STREAM_MUSIC )

            it.setDataSource( this.mediaFile )

            it.prepareAsync()
        }
    }

    private fun playMedia()
    {
        this.mediaPlayer?.let {
            if ( !it.isPlaying )
            {
                it.start()
            }
        }
    }

    private fun stopMedia()
    {
        this.mediaPlayer?.let {
            if ( it.isPlaying )
            {
                it.stop()
            }
        }
    }

    private fun pauseMedia()
    {
        this.mediaPlayer?.let {
            if ( it.isPlaying )
            {
                it.pause()
                this.resumePosition = it.currentPosition
            }
        }
    }

    private fun resumeMedia()
    {
        this.mediaPlayer?.let {
            if ( !it.isPlaying )
            {
                it.seekTo( this.resumePosition )
                it.start()
            }
        }
    }

    private fun requestAudioFocus(): Boolean
    {
        this.audioManager = this.getSystemService( Context.AUDIO_SERVICE ) as AudioManager
        val result = this.audioManager?.requestAudioFocus( this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN )

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun removeAudioFocus(): Boolean
    {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == this.audioManager?.abandonAudioFocus( this )
    }

    override fun onAudioFocusChange( focusChange: Int )
    {
        when( focusChange )
        {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if ( this.mediaPlayer == null )
                {
                    this.initMediaPlayer()
                }

                this.mediaPlayer?.let {
                    if ( !it.isPlaying )
                    {
                        it.start()
                    }
                    it.setVolume( 1.0F, 1.0F )
                }
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                this.mediaPlayer?.let {
                    if ( it.isPlaying )
                    {
                        it.stop()
                        it.release()
                        this.mediaPlayer = null
                    }
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                this.mediaPlayer?.let {
                    if ( it.isPlaying ) it.pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                this.mediaPlayer?.let {
                    if ( it.isPlaying ) it.setVolume( 0.1F, 0.1F )
                }
            }
        }
    }

    override fun onCompletion( mediaPlayer: MediaPlayer? )
    {
        this.stopMedia()
        this.stopSelf()
    }

    override fun onPrepared( mp: MediaPlayer? )
    {
        this.playMedia()
    }

    override fun onError( mediaPlayer: MediaPlayer?, what: Int, extra: Int ): Boolean
    {
        when( what )
        {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d( "PlaybackService", "Not valid for progressive playback - $extra" )
            }

            MediaPlayer.MEDIA_ERROR_SERVER_DIED ->
            {
                Log.d( "PlaybackService", "Server died - $extra" )
            }

            MediaPlayer.MEDIA_ERROR_UNKNOWN ->
            {
                Log.d( "PlaybackService", "Unknown - $extra" )
            }
        }

        return false
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {

    }

    private val binder: IBinder = LocalBinder()

    override fun onBind( intent: Intent? ): IBinder
    {
        return this.binder
    }

    inner class LocalBinder: Binder()
    {
        fun getService(): PlaybackService
        {
            return this@PlaybackService
        }
    }
}