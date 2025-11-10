package com.spear.iriskt.android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spear.iriskt.R

/**
 * 컨트롤러 목록을 표시하기 위한 어댑터
 */
class ControllerAdapter(
    private var controllers: MutableList<ControllerItem>,
    private val onEditClick: (ControllerItem) -> Unit,
    private val onDeleteClick: (ControllerItem) -> Unit,
    private val onToggleClick: (ControllerItem, Boolean) -> Unit
) : RecyclerView.Adapter<ControllerAdapter.ControllerViewHolder>() {

    /**
     * 컨트롤러 ?�이???�이???�래??     */
    data class ControllerItem(
        val name: String,
        val className: String,
        var isEnabled: Boolean
    )

    /**
     * 컨트롤러 �??�??     */
    inner class ControllerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewControllerName)
        val textViewClassName: TextView = itemView.findViewById(R.id.textViewControllerClassName)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        val buttonToggle: ImageButton = itemView.findViewById(R.id.buttonToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControllerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_controller, parent, false)
        return ControllerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ControllerViewHolder, position: Int) {
        val controller = controllers[position]
        
        holder.textViewName.text = controller.name
        holder.textViewClassName.text = controller.className

        // 활성화/비활성화 상태에 따라 아이콘 변경
        holder.buttonToggle.setImageResource(
            if (controller.isEnabled) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )

        // 버튼 클릭 이벤트 설정
        holder.buttonEdit.setOnClickListener {
            onEditClick(controller)
        }

        holder.buttonDelete.setOnClickListener {
            onDeleteClick(controller)
        }

        holder.buttonToggle.setOnClickListener {
            val newState = !controller.isEnabled
            controller.isEnabled = newState
            holder.buttonToggle.setImageResource(
                if (newState) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play
            )
            onToggleClick(controller, newState)
        }
    }

    override fun getItemCount(): Int = controllers.size

    /**
     * 컨트롤러 목록을 업데이트합니다.
     */
    fun updateControllers(newControllers: List<ControllerItem>) {
        controllers.clear()
        controllers.addAll(newControllers)
        notifyDataSetChanged()
    }

    /**
     * 컨트롤러를 추가합니다.
     */
    fun addController(controller: ControllerItem) {
        controllers.add(controller)
        notifyItemInserted(controllers.size - 1)
    }

    /**
     * 컨트롤러를 제거합니다.
     */
    fun removeController(position: Int) {
        if (position in 0 until controllers.size) {
            controllers.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
