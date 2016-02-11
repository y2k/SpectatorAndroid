package y2k.spectator.common

import org.robovm.apple.uikit.UITableViewCell

/**
 * Created by y2k on 2/11/16.
 */
abstract class ListCell<T> : UITableViewCell() {

    abstract fun bind(data: T)
}