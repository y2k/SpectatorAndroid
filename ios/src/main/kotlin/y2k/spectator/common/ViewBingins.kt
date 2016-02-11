package y2k.spectator.common

import org.robovm.apple.foundation.NSURL
import org.robovm.apple.foundation.NSURLRequest
import org.robovm.apple.uikit.*
import y2k.spectator.ServiceLocator
import y2k.spectator.model.Image

/**
 * Created by y2k on 2/11/16.
 */

fun UIWebView.bindUrl(binding: Binding<String>) {
    binding.subject.subscribe { loadRequest(NSURLRequest(NSURL(it))) }
    loadRequest(NSURLRequest(NSURL(binding.value))) // TODO:
}

fun UIWebView.bindTitle(binding: Binding<String>) {
    delegate = object : UIWebViewDelegateAdapter() {

        override fun didFinishLoad(webView: UIWebView?) {
            binding.value = evaluateJavaScript("document.title")
        }
    }
}

fun UIView.bind(binding: Binding<Boolean>) {
    binding.subject.subscribe { isHidden = !it }
}

fun UILabel.bind(text: String) {
    this.text = text
}

fun UIImageView.bind(image: Image) {
    ServiceLocator
        .resolveImageService<UIImage>()
        .get(image.normalize(frame.width.toInt(), frame.height.toInt()))
        .subscribe { this.image = it }
}

fun <T, TC : ListCell<T>> UITableView.bind(binding: Binding<List<T>>) {
    val source = ListDataSource.Default<T, TC>(this);
    binding.subject.subscribe { source.update(it) }
    dataSource = source
}