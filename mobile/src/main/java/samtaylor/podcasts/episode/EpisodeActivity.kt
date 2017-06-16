package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.PlaybackFragment
import samtaylor.podcasts.playback.play.PlayButtonFragment

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

            val playButtonFragment = PlayButtonFragment.newInstance( episodeId, it?.title, it?.show?.title )
            this.supportFragmentManager.beginTransaction().add( R.id.play_button_container, playButtonFragment ).commit()

            val playbackFragment = PlaybackFragment.newInstance( episodeId, it?.title, it?.show?.title )
            this.supportFragmentManager.beginTransaction().add( R.id.playback_fragment_container, playbackFragment ).commit()
        } )
    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
