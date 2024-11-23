package com.ifmg.gerenciadordetarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//classe responsável por atualizar a pagina de exibição das tarefas com recyclerview
class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = ArrayList()
    private var onEditClickListener: ((Task) -> Unit)? = null
    private var onDeleteClickListener: ((Task) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.bind(currentTask)

        holder.btnEdit.setOnClickListener {
            onEditClickListener?.invoke(currentTask)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClickListener?.invoke(currentTask)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    fun setOnEditClickListener(listener: (Task) -> Unit) {
        onEditClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Task) -> Unit) {
        onDeleteClickListener = listener
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(task: Task) {
            textViewDescription.text = task.description
        }
    }
}
