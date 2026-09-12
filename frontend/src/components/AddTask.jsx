import { useState } from 'react'
import DeadlineInput from './DeadlineInput.jsx'

function AddTask({ categories, draft, onAdd }) {
    const [title, setTitle] = useState(draft?.title ?? '')
    const [category, setCategory] = useState(draft?.category ?? '')
    const [deadline, setDeadline] = useState(draft?.deadline ?? null)

    async function handleSubmit(e) {
        e.preventDefault()
        
        await onAdd({ title, category: category || null, deadline })
        setTitle('')
        setCategory('')
        setDeadline(null)
    }

    return (
        <form id="task-add-panel" onSubmit={handleSubmit} autoComplete="off">
            <input value={title} onChange={e => setTitle(e.target.value)} placeholder="Task Title" required />
            <DeadlineInput value={deadline} onChange={setDeadline} />
            <select value={category} onChange={e => setCategory(e.target.value)}>
                <option value="">No category</option>
                {categories.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            <button type="submit">Add</button>
        </form>
    )
}

export default AddTask