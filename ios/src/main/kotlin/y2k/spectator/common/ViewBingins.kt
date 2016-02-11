package y2k.spectator.common

import org.robovm.apple.uikit.UIView

/**
 * Created by y2k on 2/11/16.
 */

fun <T> ListDataSource<T>.bind(binding: Binding<List<T>>) {
    binding.subject.subscribe { update(it) }
}

fun UIView.bind(binding: Binding<Boolean>) {
    binding.subject.subscribe { isHidden = !it }
}