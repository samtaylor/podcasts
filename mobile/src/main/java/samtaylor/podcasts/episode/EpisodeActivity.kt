package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.play.PlayButtonFragment

class EpisodeActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_episode )

        val episodeId = this.intent.extras[ EXTRA_EPISODE_ID ] as Int
        val viewModel = ViewModelProviders.of( this )[ EpisodeViewModel::class.java ]

        viewModel.getEpisode( episodeId ).observe( this, Observer { episode ->

            val episodeName = findViewById( R.id.episode_name ) as TextView
            episode?.let {
                this.title = it.title

                episodeName.text = it.title

                val playButtonFragment = PlayButtonFragment.newInstance( episodeId )
                this.supportFragmentManager.beginTransaction().replace( R.id.play_button_container, playButtonFragment ).commit()
            }
        } )
    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
