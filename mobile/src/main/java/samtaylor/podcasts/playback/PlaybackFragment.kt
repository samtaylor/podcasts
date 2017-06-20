package samtaylor.podcasts.playback

import android.arch.lifecycle.LifecycleFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.dataModel.Episode
import samtaylor.podcasts.playback.play.PlayButtonFragment

class PlaybackFragment: LifecycleFragment()
{
    private val serviceConnection = PlaybackServiceConnection { serviceConnection, _ ->

        this.update( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING ||
                     serviceConnection.playbackState == PlaybackService.PlaybackState.PAUSED,
                     serviceConnection.currentEpisode )
    }

    private var playbackServiceBroadcastReceiver = PlaybackServiceBroadcastReceiver { action, _ ->

        this.update( action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY ||
                     action == PlaybackService.BROADCAST_PLAYBACK_SERVICE_PAUSE,
                     this.serviceConnection.currentEpisode )
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        this.context.bindService( Intent( this.context, PlaybackService::class.java ),
                                  this.serviceConnection,
                                  Context.BIND_AUTO_CREATE )
    }

    override fun onResume()
    {
        super.onResume()

        this.update( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING ||
                     serviceConnection.playbackState == PlaybackService.PlaybackState.PAUSED,
                     serviceConnection.currentEpisode )

        this.playbackServiceBroadcastReceiver.register( this.context )
    }

    override fun onPause()
    {
        super.onPause()

        this.playbackServiceBroadcastReceiver.unregister( this.context )
    }

    private fun update( isPlaying: Boolean, episode: Episode? )
    {
        this.view?.visibility = View.GONE

        episode?.let {
            this.view?.visibility = if ( isPlaying ) View.VISIBLE else View.GONE

            val playButtonFragment = PlayButtonFragment.newInstance( it.episode_id )
            this.fragmentManager.beginTransaction().replace( R.id.fragment_play_button_container, playButtonFragment ).commit()

            val showName = this.activity.findViewById( R.id.playing_show_name ) as TextView
            val episodeName = this.activity.findViewById( R.id.playing_episode_name ) as TextView

            showName.text = it.show.title
            episodeName.text = it.title
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        this.context.unbindService( this.serviceConnection )
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_playback, container, false )

        return rootView
    }
}