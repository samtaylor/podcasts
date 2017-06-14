package samtaylor.podcasts.playback

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.episode.EpisodeViewModel

class PlaybackFragment: LifecycleFragment()
{

    private var playbackService: PlaybackService? = null

    private var serviceBound = false

    private val serviceConnection = PlaybackServiceConnection()

    inner class PlaybackServiceConnection: ServiceConnection
    {
        override fun onServiceConnected( name: ComponentName?, service: IBinder? )
        {
            val binder = service as PlaybackService.LocalBinder
            this@PlaybackFragment.playbackService = binder.getService()

            this@PlaybackFragment.serviceBound = true
        }

        override fun onServiceDisconnected( name: ComponentName? )
        {
            this@PlaybackFragment.serviceBound = false
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        this.retainInstance = true
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_playback, container, false )

        val forceShow = this.arguments[ ARG_FORCE_SHOW ] as Boolean
        val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int

        val playButton = rootView.findViewById( R.id.play_button ) as ImageButton
        playButton.setOnClickListener {
            if ( !this.serviceBound )
            {
                val playIntent = Intent( this.context, PlaybackService::class.java )
                playIntent.putExtra( "media", "https://api.spreaker.com/v2/episodes/$episodeId/play" )
                this.context.startService( playIntent )
                this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
            }
        }

        val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]
        viewModel.getEpisode( episodeId ).observe( this, Observer {
            val showName = rootView.findViewById( R.id.playing_show_name ) as TextView
            val episodeName = rootView.findViewById( R.id.playing_episode_name ) as TextView

            showName.text = it?.show?.title
            episodeName.text = it?.title
        })

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