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
        when ( state )
        {
            PlaybackServiceConnection.ConnectionState.CONNECTED -> {

                val playbackFragment = this.activity.findViewById( R.id.playback_fragment )
                when ( serviceConnection.playbackState )
                {
                    PlaybackService.PlaybackState.STOPPED -> {
                        playbackFragment.visibility = View.GONE
                    }
                    else -> {
                        playbackFragment.visibility = View.VISIBLE
                    }
                }

                val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int
                val currentEpisode = serviceConnection.currentEpisode?.let{ it } ?: episodeId

                val playButtonFragment = PlayButtonFragment.newInstance( currentEpisode )
                this.fragmentManager.beginTransaction().add( R.id.fragment_play_button_container, playButtonFragment ).commit()

                val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]
                viewModel.getEpisode( currentEpisode ).observe( this, Observer {
                    val showName = this.activity.findViewById( R.id.playing_show_name ) as TextView
                    val episodeName = this.activity.findViewById( R.id.playing_episode_name ) as TextView

                    showName.text = it?.show?.title
                    episodeName.text = it?.title
                } )

            }
            else -> {}
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        val intent = Intent( this.context, PlaybackService::class.java )
        this.context.bindService( intent, this.serviceConnection, Context.BIND_AUTO_CREATE )
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