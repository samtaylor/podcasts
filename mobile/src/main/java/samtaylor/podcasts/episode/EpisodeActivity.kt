package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.PlaybackService

class EpisodeActivity : LifecycleActivity()
{
    private var playbackService: PlaybackService? = null

    private var serviceBound = false

    private val serviceConnection = PlaybackServiceConnection()

    inner class PlaybackServiceConnection: ServiceConnection
    {
        override fun onServiceConnected( name: ComponentName?, service: IBinder? )
        {
            val binder = service as PlaybackService.LocalBinder
            this@EpisodeActivity.playbackService = binder.getService()

            this@EpisodeActivity.serviceBound = true
        }

        override fun onServiceDisconnected( name: ComponentName? )
        {
            this@EpisodeActivity.serviceBound = false
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_episode )

        val episodeId = this.intent.extras[ EXTRA_EPISODE_ID ] as Int
        val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]

        viewModel[ episodeId ].observe( this, Observer {

            val episodeName = findViewById( R.id.episode_name ) as TextView
            episodeName.text = it?.title
        } )
    }

    fun playPauseButtonClicked( button: View )
    {
        if ( button is Button )
        {
            togglePlayback( button )
        }
    }

    private fun togglePlayback( button: Button )
    {
        this.play()
    }

    private fun play()
    {
        if ( !this.serviceBound )
        {
            val episodeId = this.intent.extras[ EXTRA_EPISODE_ID ] as Int

            val playIntent = Intent( this, PlaybackService::class.java )
            playIntent.putExtra( "media", "https://api.spreaker.com/v2/episodes/$episodeId/play" )
            this.startService( playIntent )
            this.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
        }
    }

    private fun pause()
    {

    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
