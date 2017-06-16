package samtaylor.podcasts.playback

import android.content.Context
import android.media.session.MediaSessionManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat

class PlaybackServiceNotification( context: Context )
{
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    init {
        this.mediaSessionManager = context.getSystemService( Context.MEDIA_SESSION_SERVICE ) as MediaSessionManager
        this.mediaSession = MediaSessionCompat( context.applicationContext, "PodcastPlayer" )

        this.mediaSession?.let { mediaSession ->

            this.transportControls = mediaSession.controller.transportControls
            mediaSession.isActive = true
            mediaSession.setFlags( MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS )

            this.updateMetaData()

            mediaSession.setCallback( object: MediaSessionCompat.Callback() {

                override fun onPlay()
                {
                    super.onPlay()
                }

                override fun onPause()
                {
                    super.onPause()
                }

                override fun onStop()
                {
                    super.onStop()
                }

                override fun onSeekTo( pos: Long )
                {
                    super.onSeekTo(pos)
                }
            } )
        }
    }

    private fun updateMetaData()
    {

    }
}