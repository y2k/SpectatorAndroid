package y2k.spectator.binding

import android.app.Activity
import android.support.v4.view.ViewPager
import android.view.View
import android.webkit.WebSettings
import y2k.spectator.common.Binding
import y2k.spectator.common.DslRecyclerView
import y2k.spectator.model.Page

/**
 * Created by y2k on 2/21/16.
 */

fun bindingBuilder(root: ViewResolver, init: BindingBuilder.() -> Unit) {
    TODO()
}

fun bindingBuilder(root: Activity, init: BindingBuilder.() -> Unit) {
    BindingBuilder().init()
}

class BindingBuilder {

    fun <T> action(binding: Binding<T>, f: (T) -> Unit) {
        TODO()
    }

    fun <T> recyclerView(id: Int, binding: Binding<List<T>>, f: DslRecyclerView<T>.() -> Unit) {
        TODO()
    }

    fun loadingProgress(id: Int, binding: Binding<Boolean>) {
        TODO()
    }

    fun editText(id: Int, binding: Binding<String>) {
        TODO()
    }

    fun visibility(id: Int, binding: Binding<Boolean>, invert: Boolean = false) {
        TODO()
    }

    //    fun url(id: Int, binding: Binding<String>) {
    //        throw UnsupportedOperationException("not implemented") // FIXME:
    //    }

    fun webView(id: Int, init: WebViewBinding.() -> Unit) {
        throw UnsupportedOperationException("not implemented") // FIXME:
    }

    fun webView(id: Int, binding: Binding<Page>) {
        throw UnsupportedOperationException("not implemented") // FIXME:
    }

    fun viewPager(view: ViewPager, binding: Binding<Int>) {
        TODO()
    }
}

class WebViewBinding {

    val settings: WebSettings
        get() = TODO()

    fun url(binding: Binding<String>) {
        TODO()
    }

    fun title(binding: Binding<String>) {
        TODO()
    }
}

interface ViewResolver {
    fun <T : View> find(id: Int): T
}