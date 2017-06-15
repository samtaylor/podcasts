package samtaylor.podcasts.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PlaybackServiceBroadcastReceiver( private val callback: ( String, Int ) -> Unit) : BroadcastReceiver()
{
    override fun onReceive( context: Context?, intent: Intent? )
    {
        intent?.action?.let {
            this.callback( intent.action, intent.extras[ PlaybackService.EXTRA_EPISODE_ID ] as Int )
        }

    }
}