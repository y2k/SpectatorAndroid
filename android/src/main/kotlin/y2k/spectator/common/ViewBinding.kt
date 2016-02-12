package y2k.spectator.common

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import y2k.spectator.model.Page

/**
 * Created by y2k on 2/10/16.
 */

fun View.bind(binding: Binding<Boolean>) {
    binding.subject.subscribe {
        if (it) visibility = View.VISIBLE else visibility = View.GONE
    }
}

fun Activity.bind(id: Int, binding: Binding<Boolean>) {
    val view = findViewById(id)
    binding.subject.subscribe {
        if (it) view.visibility = View.VISIBLE else view.visibility = View.GONE
    }
}

fun Activity.bindEditText(id: Int, binding: Binding<String>) {
    find<EditText>(id).bind(binding)
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

fun View.bind(id: Int, command: () -> Unit): View {
    findViewById(id).setOnClickListener { command() }
    return this
}

fun Activity.bind(id: Int, command: () -> Unit) {
    findViewById(id).setOnClickListener { command() }
}

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.bind(dataSource: Binding<List<T>>) {
    dataSource.subject.subscribe { update(it) }
}

fun WebView.bind(binding: Binding<Page>) {
    binding.subject.subscribe {
        loadDataWithBaseURL(it.baseUrl, it.data, null, null, null)
    }
}

fun WebView.bindUrl(binding: Binding<String>) {
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