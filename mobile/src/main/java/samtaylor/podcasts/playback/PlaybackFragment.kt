package samtaylor.podcasts.playback

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.episode.EpisodeViewModel
import samtaylor.podcasts.playback.play.PlayButtonFragment

class PlaybackFragment: LifecycleFragment()
{
    private val serviceConnection = PlaybackServiceConnection { serviceConnection, state ->

        this.update( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING ||
                     serviceConnection.playbackState == PlaybackService.PlaybackState.PAUSED,
                     serviceConnection.currentEpisode )
    }

    private var playbackServiceBroadcastReceiver = PlaybackServiceBroadcastReceiver { action, episodeId ->

        this.update( action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY ||
                     action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PAUSE,
                     episodeId )
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        this.context.bindService( Intent( this.context, PlaybackService::class.java ),
                                  this.serviceConnection,
                                  Context.BIND_AUTO_CREATE )

        this.playbackServiceBroadcastReceiver.register( this.context )
    }

    private fun update( isPlaying: Boolean, episodeId: Int? )
    {
        episodeId?.let {
            this.view?.visibility = if ( isPlaying ) View.VISIBLE else View.GONE

            val playButtonFragment = PlayButtonFragment.newInstance( it )
            this.fragmentManager.beginTransaction().replace( R.id.fragment_play_button_container, playButtonFragment ).commit()

            val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]
            viewModel.getEpisode( it ).observe( this, Observer {

                val showName = this.activity.findViewById( R.id.playing_show_name ) as TextView
                val episodeName = this.activity.findViewById( R.id.playing_episode_name ) as TextView

                showName.text = it?.show?.title
                episodeName.text = it?.title
            } )
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.context.unbindService( this.serviceConnection )

        this.playbackServiceBroadcastReceiver.unregister( this.context )
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_playback, container, false )

        return rootView
    }

    companion object
    {
        val PLAYBACK_FRAGMENT_TAG = "playback_fragment"

        val ARG_EPISODE_ID = "episode_id"

        fun newInstance( episodeId: Int ): PlaybackFragment
        {
            val fragment = PlaybackFragment()

            val args = Bundle()
            args.putInt( ARG_EPISODE_ID, episodeId )
            fragment.arguments = args

            return fragment
        }
    }
}