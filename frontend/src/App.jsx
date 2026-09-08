import { useEffect, useMemo, useState } from 'react'
import { getTasks } from './api.js'
import NextTask from './components/NextTask.jsx'
import TaskList from './components/TaskList.jsx'

function App() {
    const [tasks, setTasks] = useState([])
    const [categories, setCategories] = useState([])

    useEffect(() => {
        getTasks().then(d => { setTasks(d.tasks); setCategories(d.categories)})
    }, [])

    const nextTask = useMemo(() => tasks.find(t => t.deadline) ?? null, [tasks])

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
                        <div id="tabs-display"></div>
                        <TaskList tasks={tasks} onComplete={() => {}} onDelete={() => {}} onEdit={() => {}} />
                    </div>

                </section>


                <div id="edit-modal">

                    <div id="edit-panel">
                        <input type="text" id="edit-title-input" placeholder="Task title" />
                        <input type="text" id="edit-deadline-input" placeholder="Deadline" />
                        <select id="edit-category-select">
                            <option value="">No category</option>
                        </select>
                        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                            <button id="edit-cancel-btn">Cancel</button>
                            <button id="edit-save-btn">Save</button>
                        </div>
                    </div>
                    
                </div>


                
                <aside className="forms">

                    <form id="nl-add-panel" autoComplete="off">
                        <input type="text" id="nl-input" placeholder="e.g. Math quiz on friday 1pm" required />
                        <button type="submit">Parse</button>
                    </form>

                    <form id="task-add-panel" autoComplete="off">
                        <input type="text" id="title-input" placeholder="Task title" required />
                        <input type="text" id="deadline-input" placeholder="Deadline" />
                        <select id="category-select"><option value="">No category</option></select>
                        <input type="text" id="category-new-input" placeholder="New category name" style={{ display: 'none' }} />
                        <button type="submit">Add</button>
                    </form>
                    


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