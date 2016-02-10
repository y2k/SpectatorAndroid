package y2k.spectator.common

import org.robovm.apple.uikit.*

/**
 * Created by y2k on 10/02/16.
 */
class SideMenu(private val parent: UIViewController, menuStoryboardId: String) {

    private val parentView = parent.navigationController.view
    private val menuView = parent.storyboard.instantiateViewController(menuStoryboardId).view
    private val closeButton = UIButton(parentView.frame)

    init {
        closeButton.addOnTouchUpInsideListener { sender, e -> closeButtonClicked() }
    }

    private fun closeButtonClicked() {
        UIView.animate(0.3, { restoreViewPosition() }, { removeMenuViews() })
    }

    private fun restoreViewPosition() {
        parentView.subviews.forEach { it.frame = it.frame.offset(-PanelWidth, 0.0) }
    }

    private fun removeMenuViews() {
        closeButton.removeFromSuperview()
        menuView.removeFromSuperview()
    }

    fun attach() {
        val menuButton = UIBarButtonItem().apply { image = UIImage.getImage("ic_menu_white.png") }
        menuButton.setOnClickListener { menuButtonClicked() }
        parent.navigationItem.leftBarButtonItem = menuButton

        val edgeGesture = UIScreenEdgePanGestureRecognizer() { menuButtonClicked() }
        edgeGesture.edges = UIRectEdge.Left
        parent.view.addGestureRecognizer(edgeGesture)
    }

    private fun menuButtonClicked() {
        if (menuView.superview != null)
            return

        var menuFrame = parentView.frame
        menuFrame = menuFrame.setWidth(PanelWidth).setX(-PanelWidth)
        menuView.frame = menuFrame

        parentView.addSubview(menuView)
        parentView.sendSubviewToBack(menuView)

        parentView.addSubview(closeButton)

        UIView.animate(0.3) {
            menuView.frame = menuFrame
            parentView.subviews.forEach { it.frame = it.frame.offset(PanelWidth, 0.0) }
        }
    }

    companion object {

        private val PanelWidth = 280.0
    }
}