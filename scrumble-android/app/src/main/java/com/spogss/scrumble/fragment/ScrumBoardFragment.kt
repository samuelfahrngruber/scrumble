package com.spogss.scrumble.fragment

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ArrayAdapter
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.rengwuxian.materialedittext.MaterialEditText
import com.spogss.scrumble.R
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import de.mrapp.android.dialog.MaterialDialog
import de.mrapp.android.dialog.ScrollableArea


class ScrumBoardFragment: BoardViewFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutRes = R.layout.fragment_scrum_board
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_task, menu)
        val icon = menu.getItem(0).icon // change 0 with 1,2 ...
        icon.mutate()
        icon.setColorFilter(ContextCompat.getColor(context!!, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_add_task -> {
                setupPopup()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupPopup() {
        val dialogBuilder = MaterialDialog.Builder(context!!)
        dialogBuilder.setTitle(R.string.add_task)
        dialogBuilder.setTitleColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setPositiveButton(android.R.string.ok, null)
        dialogBuilder.setNegativeButton(android.R.string.cancel, null)
        dialogBuilder.setButtonTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialogBuilder.setCanceledOnTouchOutside(false)

        val customView = View.inflate(context, R.layout.popup_add_task, null)

        val toggleSwitch = customView.findViewById<ToggleSwitch>(R.id.popup_add_task_toggle_button)
        toggleSwitch.setCheckedPosition(0)
        //TODO: Disable ToggleSwitch when currentSprint = null

        setupSpinner(customView)

        dialogBuilder.setScrollableArea(ScrollableArea.Area.CONTENT)
        dialogBuilder.setView(customView)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(MaterialDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
                if(dialogButtonClick(customView))
                    dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun setupSpinner(customView: View) {
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        responsibleSpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))

        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)
        verifySpinner.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, users))
    }

    private fun dialogButtonClick(customView: View): Boolean {
        val nameEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_name)
        val infoEditText = customView.findViewById<MaterialEditText>(R.id.popup_add_task_info)
        val responsibleSpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_responsible)
        val verifySpinner = customView.findViewById<MaterialBetterSpinner>(R.id.popup_add_task_verify)

        var closePopup = true
        if(nameEditText.text.trim().isEmpty()) {
            nameEditText.error = resources.getString(R.string.error_enter_name)
            closePopup = false
        }
        if(infoEditText.text.trim().isEmpty()) {
            infoEditText.error = resources.getString(R.string.error_enter_info)
            closePopup = false
        }
        if(responsibleSpinner.text.trim().isEmpty()) {
            responsibleSpinner.error = resources.getString(R.string.error_select_team_member)
            closePopup = false
        }
        if(verifySpinner.text.trim().isEmpty()) {
            verifySpinner.error = resources.getString(R.string.error_select_team_member)
            closePopup = false
        }

        return closePopup
    }
}