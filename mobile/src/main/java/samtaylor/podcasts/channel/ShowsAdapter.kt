package samtaylor.podcasts.channel

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import samtaylor.podcasts.dataModel.Show
import samtaylor.podcasts.shows.ShowActivity

class ShowsAdapter(val shows: List<Show>? ) : RecyclerView.Adapter<ShowViewHolder>()
{
    override fun onCreateViewHolder(viewGroup: ViewGroup?, viewType: Int ): ShowViewHolder
    {
        val textView = LayoutInflater.from( viewGroup?.context ).inflate( android.R.layout.simple_list_item_1, viewGroup, false )

        return ShowViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ShowViewHolder?, position: Int )
    {
        val textView = viewHolder?.view as TextView
        val show = shows?.get( position )

        textView.text = show!!.title
        textView.setOnClickListener { _ ->

            val intent = Intent(textView.context, ShowActivity::class.java)
            intent.putExtra(ShowActivity.EXTRA_SHOW_ID, show.show_id )
            textView.context.startActivity( intent )
        }
    }

    override fun getItemCount(): Int
    {
        return shows?.size ?: 0
    }
}