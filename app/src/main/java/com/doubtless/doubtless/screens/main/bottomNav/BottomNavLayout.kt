package com.doubtless.doubtless.screens.main.bottomNav

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

class BottomNavLayout(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private var currentSelectedIndex: Int? = null
    private val elements: ArrayList<BottomIntractableElement> = arrayListOf()
    private var onSelectedItemChangedListener: OnSelectedItemChangedListener? = null


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        elements.addAll(findBottomNavElements())

        // set initial selected state
        if (currentSelectedIndex == null) { // not restored after process death.
            currentSelectedIndex = BottomNavMenu.defaultSelectedIndex
        }

        onNewSelectedIndex(currentSelectedIndex!!)
        notifyAllUnSelectedElements()

        // set click listeners on each button
        elements.forEachIndexed { idx, btn ->
            (btn as View).setOnClickListener {
                setSelectStateOfSelectedItemAndNotifyUnselectedForElse(idx, btn)
            }
        }
    }

    private fun onNewSelectedIndex(index: Int) {
        onSelectedItemChangedListener?.onNewSelectedIndex(index)
        elements[index].onSelected()
    }

    private fun notifyAllUnSelectedElements() {
        // trigger onUnselected callback for rest of the elements.
        elements.forEach { element ->
            if (element != elements[currentSelectedIndex!!]) {
                element.onUnselected()
                performUiUpdateOnIndexUnSelection(element)
            }
        }
    }

    private fun setSelectStateOfSelectedItemAndNotifyUnselectedForElse(
        idx: Int,
        clickedBtn: BottomIntractableElement
    ) {
        // proceed on only new index selection.
        if (currentSelectedIndex == idx) {
            clickedBtn.onReselected()
            return
        }

        currentSelectedIndex = idx
        onSelectedItemChangedListener?.onNewSelectedIndex(idx)

        clickedBtn.onSelected()
        performUiUpdateOnIndexSelection(idx)

        notifyAllUnSelectedElements()
    }

    private fun performUiUpdateOnIndexSelection(selectedIndex: Int) {
//        (elements[selectedIndex] as View).layoutParams =
//            LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    }

    private fun performUiUpdateOnIndexUnSelection(element: BottomIntractableElement) {
//        (element as View).layoutParams =
//            LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f)
    }

    private fun findBottomNavElements(): List<BottomIntractableElement> {
        val elementsFound = mutableListOf<BottomIntractableElement>()

        this.children.forEach {
            if (it is BottomIntractableElement) {
                elementsFound.add(it)
            }
        }

        return elementsFound
    }

    fun getCurrentSelectedIndex(): Int {
        return currentSelectedIndex!!
    }

    fun setOnSelectedItemChangedListener(onSelectedItemChangedListener: OnSelectedItemChangedListener) {
        this.onSelectedItemChangedListener = onSelectedItemChangedListener
    }

    // ---------------------------------
    // ------- save and restore --------

//    override fun onSaveInstanceState(): Parcelable {
//        super.onSaveInstanceState()
//
//        val bundle = Bundle()
//
//        if (currentSelectedIndex != null)
//            bundle.putInt("curr_selected", currentSelectedIndex!!)
//
//        return bundle
//    }

//    override fun onRestoreInstanceState(state: Parcelable?) {
//        super.onRestoreInstanceState(state)
//
//        if (state is Bundle) {
//            currentSelectedIndex = state.getInt("curr_selected")
//        }
//    }

    // ------- save and restore --------
    // ---------------------------------
}