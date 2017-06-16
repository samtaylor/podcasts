package samtaylor.podcasts.playback.play

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
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

                this.update( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING,
                             serviceConnection.playbackState == PlaybackService.PlaybackState.LOADING,
                             serviceConnection.currentEpisode )
            }
            else -> {}
        }
    }

    private val playbackServiceBroadcastReceiver = PlaybackServiceBroadcastReceiver { action, episodeId ->

        this.update( action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY,
                     action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_LOAD,
                     episodeId )
    }

    private fun update( isPlaying: Boolean, isLoading: Boolean, episodeId: Int? )
    {
        val button = this.view?.findViewById( R.id.play_button ) as ImageButton
        val spinner = this.view?.findViewById( R.id.loading ) as ProgressBar

        button.setImageResource( android.R.drawable.ic_media_play )
        if ( episodeId == this.arguments[ ARG_EPISODE_ID ] )
        {
            if ( isPlaying ) { button.setImageResource( android.R.drawable.ic_media_pause ) }
        }

        if ( isLoading )
        {
            button.visibility = View.GONE
            spinner.visibility = View.VISIBLE
        }
        else
        {
            button.visibility = View.VISIBLE
            spinner.visibility = View.GONE
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        this.context.bindService( Intent( this.context, PlaybackService::class.java ),
                                  this.serviceConnection,
                                  Context.BIND_AUTO_CREATE )

        this.playbackServiceBroadcastReceiver.register( this.context )
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.context.unbindService( this.serviceConnection )

        this.playbackServiceBroadcastReceiver.unregister( this.context )
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_play_button, container, false )

        val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int

        val button = rootView.findViewById( R.id.play_button ) as ImageButton

        button.setOnClickListener {
            if ( episodeId == this.serviceConnection.currentEpisode )
            {
                when ( this.serviceConnection.playbackState )
                {
                    PlaybackService.PlaybackState.PLAYING -> {

                        this.context.sendBroadcast( Intent( PlaybackService.ACTION_PAUSE ) )
                    }

                    PlaybackService.PlaybackState.PAUSED -> {

                        this.context.sendBroadcast( Intent( PlaybackService.ACTION_RESUME ) )
                    }

                    else -> {}
                }
            }
            else
            {
                val playIntent = Intent( this.context, PlaybackService::class.java )
                playIntent.putExtra( PlaybackService.EXTRA_EPISODE_ID, episodeId )
                this.context.startService( playIntent )
                this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
            }
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