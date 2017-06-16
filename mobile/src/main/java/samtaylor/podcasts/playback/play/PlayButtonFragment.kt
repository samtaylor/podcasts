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

        button.setImageResource( R.drawable.ic_play_arrow_black_48px )
        if ( episodeId == this.arguments[ ARG_EPISODE_ID ] )
        {
            if ( isPlaying ) { button.setImageResource( R.drawable.ic_pause_black_48px ) }
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

                    else -> {

                        this.playEpisode( episodeId, this.arguments[ ARG_EPISODE_TITLE ] as String, this.arguments[ ARG_SHOW_TITLE ] as String )
                    }
                }
            }
            else
            {
                this.playEpisode( episodeId, this.arguments[ ARG_EPISODE_TITLE ] as String, this.arguments[ ARG_SHOW_TITLE ] as String )
            }
        }

        return rootView
    }

    private fun playEpisode( episodeId: Int, episodeTitle: String = "episodeTitle", showTitle: String = "showTitle" )
    {

        val playIntent = Intent( this.context, PlaybackService::class.java )
        playIntent.putExtra( PlaybackService.EXTRA_EPISODE_ID, episodeId )
        playIntent.putExtra( PlaybackService.EXTRA_EPISODE_TITLE, episodeTitle )
        playIntent.putExtra( PlaybackService.EXTRA_SHOW_TITLE, showTitle )
        this.context.startService( playIntent )
        this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
    }

    companion object
    {
        val ARG_EPISODE_ID = "episode_id"
        val ARG_EPISODE_TITLE = "episode_title"
        val ARG_SHOW_TITLE = "show_title"

        fun newInstance( episodeId: Int, episodeTitle: String?, showTitle: String? ): PlayButtonFragment
        {
            val fragment = PlayButtonFragment()

            val args = Bundle()
            args.putInt( ARG_EPISODE_ID, episodeId )
            args.putString( ARG_EPISODE_TITLE, episodeTitle )
            args.putString( ARG_SHOW_TITLE, showTitle )
            fragment.arguments = args

            return fragment
        }
    }
}