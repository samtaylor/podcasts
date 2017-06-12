package samtaylor.podcasts.dataModel

class Episode( episode_id: Int,
               val title: String,
               duration: Int,
               explicit: Boolean,
               site_url: String,
               image_url: String,
               image_original_url:
               String, published_at:
               String, download_enabled: Boolean )
