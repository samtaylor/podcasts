package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.PlaybackFragment

class EpisodeActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_episode )

        val episodeId = this.intent.extras[ EXTRA_EPISODE_ID ] as Int
        val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]

        viewModel.getEpisode( episodeId ).observe( this, Observer {

            val episodeName = findViewById( R.id.episode_name ) as TextView
            episodeName.text = it?.title
        } )

        var playbackFragment = this.supportFragmentManager.findFragmentByTag( PlaybackFragment.PLAYBACK_FRAGMENT_TAG )
        if ( playbackFragment == null )
        {
            playbackFragment = PlaybackFragment.newInstance( true, episodeId )

            this.supportFragmentManager.beginTransaction().add( R.id.playback_fragment_container,
                                                                playbackFragment,
                                                                PlaybackFragment.PLAYBACK_FRAGMENT_TAG ).commit()
        }

    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
