package y2k.spectator.common

import org.robovm.apple.uikit.UIButton

/**
 * Created by y2k on 2/11/16.
 */

fun UIButton.addOnClick(func: () -> Unit) {
    addOnTouchUpInsideListener { sender, e -> func() }
}