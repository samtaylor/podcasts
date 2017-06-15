package samtaylor.podcasts.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class PlaybackServiceBroadcastReceiver( private val callback: ( String, Int ) -> Unit) : BroadcastReceiver()
{
    override fun onReceive( context: Context?, intent: Intent? )
    {
        intent?.action?.let {
            this.callback( intent.action, intent.extras[ PlaybackService.EXTRA_EPISODE_ID ] as Int )
        }
    }

    fun register( context: Context )
    {
        context.registerReceiver( this, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_PAUSE ) )
        context.registerReceiver( this, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_PLAY ) )
        context.registerReceiver( this, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_STOP ) )
        context.registerReceiver( this, IntentFilter( PlaybackService.BROADCAST_PLAYBACK_SERVICE_LOAD ) )
    }

    fun unregister( context: Context )
    {
        context.unregisterReceiver( this )
    }
}