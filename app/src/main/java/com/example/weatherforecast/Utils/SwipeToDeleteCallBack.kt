import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * A callback class to handle swipe-to-delete functionality in a RecyclerView.
 *
 * @param onSwipedAction A lambda function that defines the action to be performed when an item is swiped.
 * @param backgroundColor The background color of the delete action (default is a semi-transparent red).
 * @param iconResId The resource ID of the delete icon to be displayed during the swipe action.
 * @param cornerRadius The corner radius for rounding the background rectangle (default is 28f).
 */
class SwipeToDeleteCallback(
    private val onSwipedAction: (position: Int) -> Unit,
    private val backgroundColor: Int = Color.parseColor("#8AFF0000"),
    private val iconResId: Int,
    private val cornerRadius: Float = 28f
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    // Drawable for the delete icon
    private lateinit var deleteIcon: android.graphics.drawable.Drawable
    private var intrinsicWidth = 0
    private var intrinsicHeight = 0

    // Paint object to define the style of the background
    private val backgroundPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    /**
     * Called when an item is moved. This implementation does not support item movement.
     *
     * @param recyclerView The RecyclerView where the item is being moved.
     * @param viewHolder The ViewHolder of the item being moved.
     * @param target The ViewHolder of the target item.
     * @return Always returns false as item movement is not handled.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Called when an item has been swiped away.
     *
     * @param viewHolder The ViewHolder of the item that was swiped.
     * @param direction The direction of the swipe.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Perform the action defined in the onSwipedAction lambda with the adapter position
        onSwipedAction(viewHolder.adapterPosition)
    }

    /**
     * Called when child views are drawn. This method is responsible for drawing the background
     * and the delete icon during the swipe action.
     *
     * @param c The Canvas on which to draw.
     * @param recyclerView The RecyclerView where the swipe occurred.
     * @param viewHolder The ViewHolder of the item being swiped.
     * @param dX The horizontal displacement of the swipe.
     * @param dY The vertical displacement of the swipe (not used).
     * @param actionState The current action state (active, idle).
     * @param isCurrentlyActive Indicates whether the item is currently being actively swiped.
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val cardMarginHorizontal = 16
        val paddingVertical = 50

        // Load the delete icon drawable
        deleteIcon = ContextCompat.getDrawable(recyclerView.context, iconResId)!!
        intrinsicWidth = deleteIcon.intrinsicWidth
        intrinsicHeight = deleteIcon.intrinsicHeight

        // Check if the swipe action is cancelled
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            // Clear the canvas if the swipe is cancelled
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the background for the delete action
        val left = itemView.right + dX.toInt() + cardMarginHorizontal
        val right = itemView.right - cardMarginHorizontal
        val top = itemView.top + paddingVertical
        val bottom = itemView.bottom
        val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())

        // Draw the rounded rectangle
        c.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)

        // Calculate dimensions for the delete icon
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Set bounds for the delete icon and draw it
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        // Call the superclass method to handle default behavior
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Clears the specified area on the canvas.
     *
     * @param c The Canvas to clear.
     * @param left The left boundary of the area to clear.
     * @param top The top boundary of the area to clear.
     * @param right The right boundary of the area to clear.
     * @param bottom The bottom boundary of the area to clear.
     */
    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, Paint())
    }
}
