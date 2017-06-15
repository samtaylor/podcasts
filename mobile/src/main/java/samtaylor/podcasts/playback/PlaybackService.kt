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
    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }

    enum class PlaybackState
    {
        STOPPED, PAUSED, PLAYING
    }

    var playbackState = PlaybackState.STOPPED

    private var mediaPlayer: MediaPlayer? = null

    var episodeId: Int? = null

    private var resumePosition = 0

    private var audioManager: AudioManager? = null

    override fun onStartCommand( intent: Intent?, flags: Int, startId: Int ): Int
    {
        this.episodeId = intent?.extras?.get( EXTRA_EPISODE_ID ) as? Int

        if ( this.episodeId != null )
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

            it.setDataSource( "https://api.spreaker.com/v2/episodes/${this.episodeId}/play" )

            it.prepareAsync()
        }
    }

    private fun playMedia()
    {
        this.mediaPlayer?.let {
            if ( !it.isPlaying )
            {
                if ( this.resumePosition != 0 )
                {
                    this.resumeMedia()
                }
                else
                {
                    it.start()
                    this.playbackState = PlaybackState.PLAYING
                }
            }
        }
    }

    private fun stopMedia()
    {
        this.mediaPlayer?.let {
            if ( it.isPlaying )
            {
                it.stop()
                this.resumePosition = 0
                this.playbackState = PlaybackState.STOPPED
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
                this.playbackState = PlaybackState.PAUSED
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
                this.playbackState = PlaybackState.PLAYING
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
                    this.playMedia()
                    it.setVolume( 1.0F, 1.0F )
                }
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                this.mediaPlayer?.let {
                    this.stopMedia()
                    it.release()
                    this.mediaPlayer = null
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                this.mediaPlayer?.let {
                    this.pauseMedia()
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