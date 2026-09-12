import { useState } from 'react'

function CategoryManager({ categories, onAdd, onDelete }) {
    const [name, setName] = useState('')

    async function handleSubmit(e) {
        e.preventDefault()
        const trimmed = name.trim()
        if (!trimmed) return
        await onAdd(trimmed)
        setName('')
    }


    return (
        <section className="category-panel">

            <form id="add-category-form" onSubmit={handleSubmit} autoComplete="off">
                <input value={name} onChange={e => setName(e.target.value)} placeholder="New category" required />
                <button type="submit">+ Category</button>
            </form>

            <ul id="category-list">
                {categories.map(c => (
                    <li key={c}>
                        <span>{c}</span>
                        <button onClick={() => onDelete(c)}>x</button>
                    </li>
                ))}
            </ul>

        </section>
    )
}

export default CategoryManager