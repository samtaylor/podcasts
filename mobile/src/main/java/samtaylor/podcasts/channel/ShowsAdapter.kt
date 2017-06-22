package samtaylor.podcasts.channel

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import samtaylor.podcasts.R
import samtaylor.podcasts.dataModel.Show
import samtaylor.podcasts.shows.ShowActivity

class ShowsAdapter(val shows: List<Show>? ) : RecyclerView.Adapter<ShowViewHolder>()
{
    override fun onCreateViewHolder( viewGroup: ViewGroup?, viewType: Int ): ShowViewHolder
    {
        val showView = LayoutInflater.from( viewGroup?.context ).inflate( R.layout.card_show, viewGroup, false )

        return ShowViewHolder( showView )
    }

    override fun onBindViewHolder( viewHolder: ShowViewHolder?, position: Int )
    {
        val viewHolder = viewHolder as ShowViewHolder
        val show = shows?.get( position )

        show?.let {
            viewHolder.showTitle = it.title
//            viewHolder.showAuthor = it.author.fullname
            viewHolder.view.setOnClickListener { _ ->

                val intent = Intent( viewHolder.view.context, ShowActivity::class.java )
                intent.putExtra( ShowActivity.EXTRA_SHOW_ID, it.show_id )
                viewHolder.view.context.startActivity( intent )
            }
        }
    }

    override fun getItemCount(): Int
    {
        return shows?.size ?: 0
    }
}