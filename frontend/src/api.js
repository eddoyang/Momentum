const JSON_HEADERS = { 'Content-type: ': 'application/json' }

//---------------- REQUESTS ----------------

export async function getTasks() {
    const data = await (await send (`/api/tasks`)).json()
    return { tasks: data.tasks.map(normalize), categories: data.categories }
}

export async function addTask({ title, category, deadline }) {
    const res = await send(`/api/tasks`, {
        method: 'POST',
        headers: JSON_HEADERS,
        body: JSON.stringify({ title, category, deadline: toBackend(deadline) }),
    })

    return normalize(await res.json())
}

export async function editTask(id, { title, category, deadline }) {
    return send(`/api/tasks/${id}`, {
        method: 'PATCH',
        headers: JSON_HEADERS,
        body: JSON.stringify({ title, category, deadline: toBackend(deadline) })
    })
}

export async function completeTask(id) {
    return send(`/api/tasks/${id}/complete`, { method: 'PATCH'})
}

export async function addCategory(name) {
    return send(`/api/tasks/categories`, { 
        method: 'POST',
        headers: JSON_HEADERS,
        body: JSON.stringify({ name })
    })
}

export async function deleteCategory(name) {
    return send(`/api/tasks/categories/${encodeURIComponent(name)}`, { method: 'DELETE' })
}

export async function reorderCategories(order) {
    return send(`/api/tasks/categories/order`, {
        method: 'PUT',
        headers: JSON_HEADERS,
        body: JSON.stringify({ order })
    })
}

export async function parseTask(text, signal) {
    const res = await send(`/api/tasks/parse`, {
        method: 'POST',
        headers: JSON_HEADERS,
        body: JSON.stringify({ text, timezone: Intl.DateTimeFormat().resolvedOptions().timeZone }),
        signal
    })

    const draft = await res.json()

    if (draft.error)
        throw new Error(draft.error)

    return draft
}

//---------------- HELPERS ----------------
const toBackend = (d) => d ? d.toISOString() : null 
const fromBackend = (s) => s ? new Date(s) : null
const normalize = (t) => ({ ...t, deadline: fromBackend(t.deadline) }) // normalizes object's date

async function send (url, options) {
    const res = await fetch(url, options)

    if (!res.ok) 
        // uses options.method if not options and options.method is not null, otherwise uses GET
        throw new Error (`${options?.method ?? `GET`} ${url} -> ${res.status}`)  

    return res
}