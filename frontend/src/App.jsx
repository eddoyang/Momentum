import { useEffect, useMemo, useState } from 'react'

import { getTasks, addTask, editTask, completeTask, deleteTask, addCategory, deleteCategory, reorderCategories } from './api.js'

import NextTask from './components/NextTask.jsx'
import TaskList from './components/TaskList.jsx'
import EditTask from './components/EditTask.jsx'
import AddTask from './components/AddTask.jsx'


function App() {
    // ---------------- STATE ----------------
    const [tasks, setTasks] = useState([])
    const [categories, setCategories] = useState([])
    const [editingTask, setEditingTask] = useState(null)
    const nextTask = useMemo(() => tasks.find(t => t.deadline) ?? null, [tasks])

    useEffect(() => {
        getTasks().then(d => { setTasks(d.tasks); setCategories(d.categories)})
    }, [])

    // ---------------- LOAD ----------------
    async function refresh() {
        const t = await getTasks()
        setTasks(t.tasks)
        setCategories(t.categories)
    }

    useEffect(() => { refresh().catch(console.error) }, [])

    // ---------------- HANDLERS ----------------
    async function handleAdd(fields) {
        await addTask(fields)
        await refresh()
    }

    function handleEdit(id) {
        setEditingTask(tasks.find(t => t.id === id))
    }

    async function handleSave(id, fields) {
        await editTask(id, fields)
        await refresh()
        setEditingTask(null)
    }


    async function handleDelete(id) {
        await deleteTask(id)
        await refresh()
    }

    async function handleComplete(id) {
        await completeTask(id)
        await refresh()
    }

    // async function handleAddCategory(name) {
    //     await addCategory(name)
    //     await refresh()
    // }

    // async function handleDeleteCategory(name) {
    //     await deleteCategory(name)
    //     await refresh()
    // }

    // async function handleReorder(next) {
    //     setCategories(next)
    //     await refresh()
    // }

    // function handleParsed(d) {
    //     setDraft(d)
    //     setDraftKey(k => k + 1)
    // }
    // ---------------- RENDER ----------------
    return (
        <main>

            <div id="main-title">
                <h1 id="app-title">Momentum</h1>
                <h2 id="app-subtitle">Personal Todo List</h2>
            </div>

            <div className="content">

                <section className="main-display">

                    <div id="upcoming-panel">
                        <h3>To Complete</h3>
                        <NextTask task={nextTask} />
                    </div>

                    <div className="task-panel">
                        <div id="tabs-display" />
                        <TaskList tasks={tasks} onComplete={handleComplete} onDelete={handleDelete} onEdit={handleEdit} />
                    </div>

                </section>

                {editingTask && (<EditTask key={editingTask.id} task={editingTask} categories={categories} onCancel={() => setEditingTask(null)} onSave={handleSave}/>)}

                <aside className="forms">

                    <form id="nl-add-panel" autoComplete="off">
                        <input type="text" id="nl-input" placeholder="e.g. Math quiz on friday 1pm" required />
                        <button type="submit">Parse</button>
                    </form>

                    <AddTask categories={categories} onAdd={handleAdd} />

                    <section className="category-panel">
                        <form id="add-category-form" autoComplete="off">
                            <input type="text" id="category-name-input" placeholder="New category" required />
                            <button type="submit">+ Category</button>
                        </form>
                        <ul id="category-list"></ul>
                    </section>

                </aside>

            </div>

        </main>
    )
}

export default App