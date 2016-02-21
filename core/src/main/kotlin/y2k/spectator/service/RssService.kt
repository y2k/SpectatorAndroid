package y2k.spectator.service

import org.jsoup.Jsoup
import rx.Observable
import y2k.spectator.model.Subscription

/**
 * Created by y2k on 2/19/16.
 */
class RssService {

    fun analyze(url: String): Observable<List<Subscription>> {
        return Observable.fromCallable {
            val document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko")
                .get()
            document
                .select("link[type~=(?i)application/(atom\\+xml|rss\\+xml)]")
                .map {
                    Subscription().apply {
                        source = it.attr("href")
                        title = it.attr("title")
                    }
                }
                .union(listOf(Subscription().apply {
                    source = url
                    title = document.title()
                }))
                .toList()
        }
    }
}