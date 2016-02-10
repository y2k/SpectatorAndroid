package y2k.spectator.common

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText

/**
 * Created by y2k on 2/10/16.
 */

fun View.bind(binding: Binding<Boolean>) {
    binding.subject.subscribe {
        if (it) visibility = View.VISIBLE else visibility = View.GONE
    }
}

fun EditText.bind(binding: Binding<String>) {
    addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            binding.value = "" + s
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun View.bind(command: () -> Unit) = setOnClickListener { command() }

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.bind(dataSource: Binding<List<T>>) {
    dataSource.subject.subscribe { update(it) }
}

fun WebView.bindLoadUrl(binding: Binding<String>) {
    binding.subject.subscribe { loadUrl(it) }
    loadUrl(binding.value) // TODO:
}

fun WebView.bindTitle(binding: Binding<String>) {
    setWebViewClient(object : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String?) {
            binding.value = view.title
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url)
            return true;
        }
    })
}