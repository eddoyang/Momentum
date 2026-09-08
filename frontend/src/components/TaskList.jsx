import { formatDeadline } from '../format.js'

function TaskList({ tasks, onComplete, onDelete, onEdit }) {
    return (
        <ul id="task-list">
            {tasks.map(task => (
                <li key={task.id}>
                    <span>{task.deadline ? `${task.title} - due ${formatDeadline(task.deadline)}` : task.title}</span>
                    <button className="complete-btn" onClick={() => onComplete(task.id)}>Complete</button>
                    <button className="delete-btn" onClick={() => onDelete(task.id)}>Delete</button>
                    <button className="edit-btn" onClick={() => onEdit(task.id)}>Edit</button>
                </li>
            ))}
        </ul>
    )
}

export default TaskList