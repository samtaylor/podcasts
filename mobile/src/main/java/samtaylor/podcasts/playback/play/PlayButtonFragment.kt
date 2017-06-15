package samtaylor.podcasts.playback.play

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.PlaybackService
import samtaylor.podcasts.playback.PlaybackServiceBroadcastReceiver
import samtaylor.podcasts.playback.PlaybackServiceConnection

class PlayButtonFragment : Fragment()
{
    private val serviceConnection = PlaybackServiceConnection { serviceConnection, state ->
        when ( state )
        {
            PlaybackServiceConnection.ConnectionState.CONNECTED -> {

                this.update( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING, serviceConnection.currentEpisode )
            }
            else -> {}
        }
    }

    private var playbackServiceBroadcastReceiver = PlaybackServiceBroadcastReceiver { action, episodeId ->

        this.update( action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY, episodeId )
    }

    private fun update( isPlaying: Boolean, episodeId: Int? )
    {
        val button = this.view?.findViewById( R.id.play_button ) as ImageButton
        button.setImageResource( android.R.drawable.ic_media_play )
        if ( episodeId == this.arguments[ ARG_EPISODE_ID ] )
        {
            if ( isPlaying ) { button.setImageResource( android.R.drawable.ic_media_pause ) }
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        this.context.bindService( Intent( this.context, PlaybackService::class.java ),
                                  this.serviceConnection,
                                  Context.BIND_AUTO_CREATE )

        this.context.registerReceiver( this.playbackServiceBroadcastReceiver, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_PAUSE ) )
        this.context.registerReceiver( this.playbackServiceBroadcastReceiver, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY ) )
        this.context.registerReceiver( this.playbackServiceBroadcastReceiver, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_STOP ) )
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.context.unbindService( this.serviceConnection )

        this.context.unregisterReceiver( this.playbackServiceBroadcastReceiver )
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_play_button, container, false )

        val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int

        val button = rootView.findViewById( R.id.play_button ) as ImageButton

        button.setOnClickListener {
            val playIntent = Intent( this.context, PlaybackService::class.java )
            playIntent.putExtra( PlaybackService.EXTRA_EPISODE_ID, episodeId )
            this.context.startService( playIntent )
            this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
        }

        return rootView
    }

    companion object
    {
        val ARG_EPISODE_ID = "episode_id"

        fun newInstance( episodeId: Int ): PlayButtonFragment
        {
            val fragment = PlayButtonFragment()

            val args = Bundle()
            args.putInt( ARG_EPISODE_ID, episodeId )
            fragment.arguments = args

            return fragment
        }
    }
}