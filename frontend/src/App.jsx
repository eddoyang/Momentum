import { useEffect, useMemo, useState } from 'react'

import { getTasks, addTask, editTask, completeTask, deleteTask, addCategory, deleteCategory, reorderCategories } from './api.js'

import NextTask from './components/NextTask.jsx'
import TaskList from './components/TaskList.jsx'
import EditTask from './components/EditTask.jsx'
import AddTask from './components/AddTask.jsx'
import CategoryManager from './components/CategoryManager.jsx'
import CategoryTabs from './components/CategoryTabs.jsx'
import QuickAdd from './components/QuickAdd.jsx'


function App() {
    // ---------------- STATE ----------------
    const [tasks, setTasks] = useState([])
    const [categories, setCategories] = useState([])
    const [editingTask, setEditingTask] = useState(null)
    const [activeCategory, setActiveCategory] = useState('All')
    const [draft, setDraft] = useState(null)
    const [draftKey, setDraftKey] = useState(0)

    const nextTask = useMemo(() => tasks.find(t => t.deadline) ?? null, [tasks])
    const visibleTasks = useMemo(() => activeCategory === 'All' ? tasks : tasks.filter(t => t.category === activeCategory), [tasks, activeCategory])

    useEffect(() => {getTasks().then(d => { setTasks(d.tasks); setCategories(d.categories)})}, [])


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
        setEditingTask(null)
    }

    async function handleComplete(id) {
        await completeTask(id)
        await refresh()
    }

    async function handleAddCategory(name) {
        await addCategory(name)
        await refresh()
    }

    async function handleDeleteCategory(name) {
        await deleteCategory(name)
        await refresh()
    }

    async function handleReorder(next) {
        setCategories(next)
        await reorderCategories(next)
    }

    function handleParsed(d) {
        setDraft(d)
        setDraftKey(k => k + 1)
    }
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
                        <CategoryTabs categories={categories} activeCategory={activeCategory} onSelect={setActiveCategory} onReorder={handleReorder} />
                        <TaskList tasks={visibleTasks} onComplete={handleComplete} onEdit={handleEdit} />
                    </div>

                </section>

                {editingTask && (<EditTask key={editingTask.id} task={editingTask} categories={categories} onCancel={() => setEditingTask(null)} onSave={handleSave} onDelete={handleDelete}/>)}

                <aside className="forms">

                    <QuickAdd onParsed={handleParsed} />
                    <AddTask key={draftKey} draft={draft} categories={categories} onAdd={handleAdd} />
                    <CategoryManager categories={categories} onAdd={handleAddCategory} onDelete={handleDeleteCategory} />

                </aside>

            </div>

        </main>
    )
}

export default App