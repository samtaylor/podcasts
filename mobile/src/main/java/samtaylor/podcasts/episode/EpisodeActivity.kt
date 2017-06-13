package samtaylor.podcasts.episode

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import samtaylor.podcasts.R

class EpisodeActivity : LifecycleActivity()
{
    private var playing = false

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

        val playPauseButton = findViewById( R.id.play_pause_button ) as Button
        playPauseButton.setOnClickListener {
            if ( playPauseButton.text == getString( R.string.play ) )
            {
                playPauseButton.text = getString( R.string.pause )
            }
            else
            {
                playPauseButton.text = getString( R.string.play )
            }
        }
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
        if ( button.text == getString( R.string.play ) )
        {
            button.text = getString( R.string.pause )
            play()
        }
        else
        {
            button.text = getString( R.string.play )
            pause()
        }
    }

    private fun play()
    {

    }

    private fun pause()
    {

    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}
