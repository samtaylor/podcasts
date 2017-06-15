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
import android.widget.ImageButton
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.episode.EpisodeViewModel

class PlaybackFragment: LifecycleFragment()
{
    private val serviceConnection = PlaybackServiceConnection { serviceConnection, state ->
        when ( state )
        {
            PlaybackServiceConnection.ConnectionState.CONNECTED -> {
                val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int
                val currentEpisode = serviceConnection.currentEpisode?.let{ it } ?: episodeId
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

        val forceShow = this.arguments[ ARG_FORCE_SHOW ] as Boolean
        val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int

        val playButton = rootView.findViewById( R.id.play_button ) as ImageButton
        playButton.setOnClickListener {
            val playIntent = Intent( this.context, PlaybackService::class.java )
            playIntent.putExtra( PlaybackService.EXTRA_EPISODE_ID, episodeId )
            this.context.startService( playIntent )
            this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
        }

        return rootView
    }

    companion object
    {
        val PLAYBACK_FRAGMENT_TAG = "playback_fragment"

        val ARG_FORCE_SHOW = "force_show"
        val ARG_EPISODE_ID = "episode_id"

        fun newInstance( forceShow: Boolean, episodeId: Int ): PlaybackFragment
        {
            val fragment = PlaybackFragment()

            val args = Bundle()
            args.putBoolean( ARG_FORCE_SHOW, forceShow )
            args.putInt( ARG_EPISODE_ID, episodeId )
            fragment.arguments = args

            return fragment
        }
    }
}