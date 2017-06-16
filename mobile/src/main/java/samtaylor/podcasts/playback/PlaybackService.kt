package samtaylor.podcasts.playback

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import samtaylor.podcasts.R


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
        val EXTRA_EPISODE_TITLE = "episode_title"
        val EXTRA_SHOW_TITLE = "show_title"

        val BROADCAST_PLAYBACK_SERVICE_PLAY  = "samtaylor.podcasts.playback.play"
        val BROADCAST_PLAYBACK_SERVICE_PAUSE = "samtaylor.podcasts.playback.pause"
        val BROADCAST_PLAYBACK_SERVICE_STOP  = "samtaylor.podcasts.playback.stop"
        val BROADCAST_PLAYBACK_SERVICE_LOAD  = "samtaylor.podcasts.playback.load"

        val ACTION_PAUSE = "samtaylor.podcasts.playback.pause.action"
        val ACTION_RESUME = "samtaylor.podcasts.playback.play.action"
        val ACTION_STOP = "samtaylor.podcasts.playback.stop.action"
    }

    enum class PlaybackState
    {
        STOPPED, PAUSED, PLAYING, LOADING
    }

    var playbackState = PlaybackState.STOPPED

    private var mediaPlayer: MediaPlayer? = null

    var episodeId: Int? = null
    var episodeTitle: String? = null
    var showTitle: String? = null

    private var resumePosition = 0

    private var audioManager: AudioManager? = null

    private var ongoingCall = false

    private val binder: IBinder = LocalBinder()

    private val actionBroadcastReceiver = object: BroadcastReceiver() {

        override fun onReceive( context: Context?, intent: Intent? )
        {
            when ( intent?.action )
            {
                ACTION_RESUME -> {

                    this@PlaybackService.resumeMedia()
                }

                ACTION_PAUSE -> {

                    this@PlaybackService.pauseMedia()
                }

                ACTION_STOP -> {

                    this@PlaybackService.stopMedia()
                }
            }
        }
    }

    override fun onStartCommand( intent: Intent?, flags: Int, startId: Int ): Int
    {
        this.episodeId = intent?.extras?.get( EXTRA_EPISODE_ID ) as? Int
        this.episodeTitle = intent?.extras?.get( EXTRA_EPISODE_TITLE ) as? String
        this.showTitle = intent?.extras?.get( EXTRA_SHOW_TITLE ) as? String

        if ( this.episodeId != null )
        {
            if ( !this.requestAudioFocus() )
            {
                this.stopSelf()
            }
            else
            {
                if ( this.playbackState == PlaybackState.PLAYING )
                {
                    this.stopMedia()
                }

                this.initMediaPlayer()
            }
        }
        else
        {
            this.stopSelf()
        }

        return super.onStartCommand( intent, flags, startId )
    }

    private val noisyBroadcastReceiver = object: BroadcastReceiver()
    {
        override fun onReceive( context: Context?, intent: Intent? )
        {
            this@PlaybackService.pauseMedia()
            this@PlaybackService.buildNotification()
        }
    }

    private val phoneStateListener = object: PhoneStateListener()
    {
        override fun onCallStateChanged( state: Int, incomingNumber: String? )
        {
            when ( state )
            {
                TelephonyManager.CALL_STATE_OFFHOOK,
                TelephonyManager.CALL_STATE_RINGING -> {

                    if ( this@PlaybackService.mediaPlayer != null )
                    {
                        this@PlaybackService.pauseMedia()
                        this@PlaybackService.ongoingCall = true
                    }
                }

                TelephonyManager.CALL_STATE_IDLE -> {

                    if (this@PlaybackService.mediaPlayer != null) {
                        if ( this@PlaybackService.ongoingCall )
                        {
                            this@PlaybackService.ongoingCall = false
                            this@PlaybackService.resumeMedia()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate()
    {
        super.onCreate()

        val intentFilter = IntentFilter( AudioManager.ACTION_AUDIO_BECOMING_NOISY )
        registerReceiver( this.noisyBroadcastReceiver, intentFilter )

        val telephonyManager = this.getSystemService( Context.TELEPHONY_SERVICE ) as TelephonyManager
        telephonyManager.listen( this.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE )

        this.registerReceiver( this.actionBroadcastReceiver, IntentFilter( PlaybackService.ACTION_PAUSE ) )
        this.registerReceiver( this.actionBroadcastReceiver, IntentFilter( PlaybackService.ACTION_RESUME ) )
        this.registerReceiver( this.actionBroadcastReceiver, IntentFilter( PlaybackService.ACTION_STOP ) )
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.mediaPlayer?.let {
            this.stopMedia()
            it.release()
        }

        this.removeAudioFocus()

        this.unregisterReceiver( this.noisyBroadcastReceiver )
        this.unregisterReceiver( this.actionBroadcastReceiver )

        val telephonyManager = this.getSystemService( Context.TELEPHONY_SERVICE ) as TelephonyManager
        telephonyManager.listen( this.phoneStateListener, PhoneStateListener.LISTEN_NONE )
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

            this.playbackState = PlaybackState.LOADING

            val intent = Intent( BROADCAST_PLAYBACK_SERVICE_LOAD )
            intent.putExtra( EXTRA_EPISODE_ID, this.episodeId )
            this.sendBroadcast( intent )
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

                val intent = Intent( BROADCAST_PLAYBACK_SERVICE_PLAY )
                intent.putExtra( EXTRA_EPISODE_ID, this.episodeId )
                this.sendBroadcast( intent )

                this.buildNotification()
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

            val intent = Intent( BROADCAST_PLAYBACK_SERVICE_STOP )
            intent.putExtra( EXTRA_EPISODE_ID, this.episodeId )
            this.sendBroadcast( intent )

            this.removeNotification()
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

                val intent = Intent( BROADCAST_PLAYBACK_SERVICE_PAUSE )
                intent.putExtra( EXTRA_EPISODE_ID, this.episodeId )
                this.sendBroadcast( intent )

                this.buildNotification()
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

                val intent = Intent( BROADCAST_PLAYBACK_SERVICE_PLAY )
                intent.putExtra( EXTRA_EPISODE_ID, this.episodeId )
                this.sendBroadcast( intent )

                this.buildNotification()
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

    private fun buildNotification()
    {
        val notificationBuilder = Notification.Builder( this )
                .setShowWhen( false )
                .setStyle( Notification.MediaStyle()
                        .setMediaSession( null )
                        .setShowActionsInCompactView() )
                .setSmallIcon( android.R.drawable.stat_sys_headset )
                .setContentText( this.showTitle )
                .setContentTitle( this.episodeTitle )

        if ( this.playbackState == PlaybackState.PLAYING )
        {
            notificationBuilder.addAction( Notification.Action.Builder( Icon.createWithResource( this, R.drawable.ic_pause_black_48px ),
                                                                        "pause",
                                                                        this.playbackAction( 0 ) ).build() )
        }
        else
        {
            notificationBuilder.addAction( Notification.Action.Builder( Icon.createWithResource( this, R.drawable.ic_play_arrow_black_48px ),
                                                                        "play",
                                                                        this.playbackAction( 1 ) ).build() )
        }

        notificationBuilder.addAction( Notification.Action.Builder( Icon.createWithResource( this, R.drawable.ic_stop_black_48px ),
                                                                    "stop",
                                                                    this.playbackAction( 2 ) ).build() )


        val notificationManager = this.getSystemService( Context.NOTIFICATION_SERVICE ) as NotificationManager
        notificationManager.notify( 161087, notificationBuilder.build() )
    }

    private fun removeNotification()
    {
        val notificationManager = this.getSystemService( Context.NOTIFICATION_SERVICE ) as NotificationManager
        notificationManager.cancel( 161087 )
    }

    private fun playbackAction( action: Int ): PendingIntent?
    {
        val playbackAction = Intent()
        when ( action )
        {
            0 -> {
                playbackAction.action = ACTION_PAUSE
            }

            1 -> {
                playbackAction.action = ACTION_RESUME
            }

            2 -> {
                playbackAction.action = ACTION_STOP
            }
        }

        return PendingIntent.getBroadcast( this, action, playbackAction, 0 )
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

    override fun onSeekComplete(mp: MediaPlayer?) { }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean { return true }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) { }


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

    inner class BecomingNoisyBroadcastReceiver: BroadcastReceiver()
    {
        override fun onReceive( context: Context?, intent: Intent? )
        {
            this@PlaybackService.pauseMedia()
            this@PlaybackService.buildNotification()
        }
    }
}