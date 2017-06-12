package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView
import samtaylor.podcasts.R

class EpisodeActivity : LifecycleActivity()
{
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

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
