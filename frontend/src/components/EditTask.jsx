import { useState } from 'react'
import DeadlineInput from './DeadlineInput.jsx'

function EditTask({ task, categories, onCancel, onSave, onDelete}) {
    const [title, setTitle] = useState(task?.title ?? '')
    const [category, setCategory] = useState(task?.category ?? '')
    const [deadline, setDeadline] = useState(task?.deadline ?? null)

    function handleSave() {
        onSave(task.id, {title, category: category || null, deadline})
    }

    return (
        
        <div id="edit-modal" className="visible">
            <div id="edit-panel">

                <input value={title} onChange={e => setTitle(e.target.value)} placeholder="Task title" />
                <DeadlineInput value={deadline} onChange={setDeadline} />
                <select value={category} onChange={e => setCategory(e.target.value)}>
                    <option value="">No category</option>
                    {categories.map(c => <option key={c} value={c}>{c}</option>)}
                </select>
                
                <div className="modal-actions">
                    <button className="delete-btn" onClick={() => onDelete(task.id)}>Delete</button>
                    <div className="modal-actions-right">
                        <button className="cancel-btn" onClick={onCancel}>Cancel</button>
                        <button className="save-btn" onClick={handleSave}>Save</button>
                    </div>
                </div>

            </div>
        </div>
    )
}

export default EditTask