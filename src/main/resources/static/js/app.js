// ---------- State ----------
let nextTaskTitle = null;
let nextTaskDeadline = null;
let allTasks = [];
let activeCategory = 'All';
let allCategories = [];
let tabSortable = null;

// ---------- Rendering ----------
async function loadTasks() {

    // testing purposes
    // const data = {
    //     tasks: [
    //         { id: '1', title: 'Finish CPSC lab',     deadline: '2026-04-25T14:00Z[UTC]' },
    //         { id: '2', title: 'Read Chapter 4',      deadline: '2026-04-25T20:30Z[UTC]' },
    //         { id: '3', title: 'Prep slides',         deadline: '2026-04-26T09:00Z[UTC]' },
    //         { id: '4', title: 'Email professor',     deadline: '2026-04-27T15:45Z[UTC]' },
    //         { id: '1', title: 'Finish CPSC lab',     deadline: '2026-04-25T14:00Z[UTC]' },
    //         { id: '2', title: 'Read Chapter 4',      deadline: '2026-04-25T20:30Z[UTC]' },
    //         { id: '3', title: 'Prep slides',         deadline: '2026-04-26T09:00Z[UTC]' },
    //         { id: '4', title: 'Email professor',     deadline: '2026-04-27T15:45Z[UTC]' },
    //         { id: '5', title: 'Submit assignment 3', deadline: '2026-04-28T23:59Z[UTC]' }
    //     ]
    // };

    const response = await fetch('/api/tasks');
    const data = await response.json();
    allTasks = data.tasks;
    renderTaskList();
}


function renderTabs() {

    // //testing purposes
    // let activeCategory = 'All';
    // let allCategories = ['Work', 'Personal', 'Errands']; // 3 example tabs
    // let tabSortable = null;
    
    const tabBar = document.getElementById('tabs-display');
    tabBar.innerHTML = '';

    const categories = ['All', ...allCategories];

    // fall back to ALL if no categories
    if (!categories.includes(activeCategory)) activeCategory = 'All';

    for (const category of categories) {
        const tab = document.createElement('button');
        tab.textContent = category;
        tab.classList.add('category-tab');

        if (category === 'All')
            tab.dataset.fixed = 'true';

        if (category === activeCategory) tab.classList.add('active');
        tab.addEventListener('click', () => {
            activeCategory = category;
            renderTabs();
            renderTaskList();
        });

        if (tabSortable) tabSortable.destroy();

        tabSortable = new Sortable(tabBar, {
            animation: 150,
            filter: '[data-fixed]',
            onMove: (evt) => !evt.related.dataset.fixed,
            onEnd: async () => {
                const order = [...tabBar.querySelectorAll('.category-tab')]
                    .map(t => t.textContent)
                    .filter(name => name !== 'All');
                allCategories = order;
                await fetch('/api/tasks/categories/order', {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({order})
                });
            }
        });

        tabBar.appendChild(tab);
    }
}

function renderTaskList() {
    const list = document.getElementById('task-list');
    list.innerHTML = '';

    const visible = allTasks.filter(task =>
        activeCategory === 'All' || (task.category || 'Uncategorized') === activeCategory
    );

    for (const task of visible) {
        const li = document.createElement('li');


        const text = document.createElement('span');
        text.textContent = `${task.title} — due ${formatDeadline(task.deadline)}`;
        li.appendChild(text);

        //complete button
        const completeButton = document.createElement('button');
        completeButton.textContent = 'Complete';
        completeButton.classList.add('complete-btn');
        completeButton.addEventListener('click', () => completeTask(task.id));
        li.appendChild(completeButton);

        //delete button
        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Delete';
        deleteButton.classList.add('delete-btn');
        deleteButton.addEventListener('click', () => deleteTask(task.id));
        li.appendChild(deleteButton);

        //edit button
        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.classList.add('edit-btn');
        editButton.addEventListener('click', () => editTask(task.id, task.title, task.deadline, task.category));
        li.appendChild(editButton);

        list.appendChild(li);
    }
}

async function loadNextTask() {
    const response = await fetch('/api/tasks/next');
    const data = await response.json();

    if (!data.title) {
        nextTaskTitle = null;
        nextTaskDeadline = null;
    } else {
        nextTaskTitle = data.title;
        nextTaskDeadline = new Date(data.deadline.replace(/\[.*\]/, ''));
    }

    updateCountdown();
}

async function loadCategories() {
    const response = await fetch('/api/tasks/categories');
    const { categories } = await response.json();

    allCategories = categories;
    renderTabs();

    const select = document.getElementById('category-select');
    const previous = select.value;

    select.innerHTML = '';
    select.appendChild(new Option('No category', ''));


    const editSelect = document.getElementById('edit-category-select');

    editSelect.innerHTML = '';
    editSelect.appendChild(new Option('No category', ''));

    for (const category of categories) {
        select.appendChild(new Option(category, category));
        editSelect.appendChild(new Option(category, category));
    }


    const list = document.getElementById('category-list');
    list.innerHTML = '';

    for (const category of categories) {
        const li = document.createElement('li');
        const span = document.createElement('span');
        span.textContent = category;
        const del = document.createElement('button');
        del.textContent = 'x'
        del.addEventListener('click', () => deleteCategory(category));
        li.append(span, del);
        list.appendChild(li);
    }
}

document.getElementById('add-category-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('category-name-input').value.trim();

    if (!name)
        return;

    await fetch('/api/tasks/categories', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name})
    });
    e.target.reset();
    loadCategories();
});

async function deleteCategory(name) {
    await fetch(`/api/tasks/categories/${encodeURIComponent(name)}`, { method: 'DELETE' });
    loadCategories();
    loadTasks();
}


const deadlinePicker = flatpickr("#deadline-input", {
    enableTime: true,
    dateFormat: "Y-m-d H:i",
    time_24hr: false
});

// ---------- API actions ----------
async function completeTask(taskId) {
    await fetch(`/api/tasks/${taskId}/complete`, { method: 'PATCH' });
    loadTasks();
    loadNextTask();
}

async function deleteTask(taskId) {
    await fetch(`/api/tasks/${taskId}`, { method: 'DELETE' });
    loadTasks();
    loadNextTask();
}



// ---------- Edit Form ----------
let editingTaskId = null;

function editTask(taskId, currentTitle, currentDeadline, currentCategory) {
    editingTaskId = taskId;
    document.getElementById('edit-title-input').value = currentTitle;

    const cleaned = currentDeadline.replace(/\[.*\]/, '');
    editDeadlinePicker.setDate(new Date(cleaned));

    document.getElementById('edit-category-select').value = currentCategory || '';
    document.getElementById('edit-modal').classList.add('visible');
}

// Cancel Edits
document.getElementById('edit-cancel-btn').addEventListener('click', () => {
    document.getElementById('edit-modal').classList.remove('visible');
});

// Save Edits
document.getElementById('edit-save-btn').addEventListener('click', async () => {
    const title = document.getElementById('edit-title-input').value;
    const deadline = editDeadlinePicker.selectedDates[0].toISOString().replace('Z', 'Z[UTC]');
    const category = document.getElementById('edit-category-select').value || null;

    await fetch(`/api/tasks`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({id: editingTaskId, title, deadline, category})
    });

    document.getElementById('edit-modal').classList.remove('visible');
    loadTasks();
    loadNextTask();
});

// edit -> deadline calendar
const editDeadlinePicker = flatpickr("#edit-deadline-input", {
    enableTime: true,
    dateFormat: "Y-m-d H:i",
    time_24hr: false
});

// ---------- Form submission ----------
document.getElementById('task-add-panel').addEventListener('submit', async (e) => {
    e.preventDefault();

    const title = document.getElementById('title-input').value;

    const category = document.getElementById('category-select').value || null;

    const localDeadline = document.getElementById('deadline-input').value;

    const deadline = new Date(localDeadline).toISOString().replace('Z', 'Z[UTC]');

    const body = {
        id: crypto.randomUUID(),
        title,
        category,
        deadline
    };

    await fetch('/api/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

    e.target.reset();
    loadTasks();
    loadNextTask();
    loadCategories();
});

// ---------- Natural-language entry ----------
document.getElementById('nl-add-panel').addEventListener('submit', async (e) => {
    e.preventDefault();

    const button = e.target.querySelector('button');
    button.disabled = true;
    button.textContent = 'Parsing...';

    try {
        const response = await fetch('/api/tasks/parse', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                text: document.getElementById('nl-input').value,
                timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
            })
        })

        const draft = await response.json();

        document.getElementById('title-input').value = draft.title ?? '';
        document.getElementById('category-select').value = draft.category ?? '';

        const picker = document.getElementById('deadline-input')._flatpickr;
        if (draft.deadline) {
            picker.setDate(new Date(draft.deadline), false);
        } else {
            picker.clear();
        }

        e.target.reset();
        document.getElementById('title-input').focus();
    } finally {
        button.disabled = false;
        button.textContent = 'Parse';
    }
});

// ---------- Helpers ----------
function formatDeadline(isoString) {
    const cleaned = isoString.replace(/\[.*\]/, '');
    const date = new Date(cleaned);

    const datePart = date.toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'long',
        day: 'numeric'
    });

    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    const timePart = `${hour}:${minute}`;

    return `${datePart} (${timePart})`;
}

function updateCountdown() {
    const content = document.getElementById('next-task');

    //no tasks
    if (!nextTaskDeadline) {
        content.textContent = 'All Tasks Completed!';
        return;
    }

    const diff = nextTaskDeadline - new Date();

    //overdue
    if (diff < 0) {
        content.textContent = `${nextTaskTitle} — Overdue`;
        return;
    }

    const minutes = Math.floor(diff / 60000);

    //less than an hour
    if (minutes < 60) {
        if (minutes === 0) {
            content.textContent = `${nextTaskTitle} — due in less than a minute`;
        } else {
            content.textContent = `${nextTaskTitle} — due in ${minutes} minute${minutes === 1 ? '' : 's'}`;
        }
        //1+ days
    } else if (minutes > 60 * 24) {
        const days = Math.floor(minutes / (60 * 24));
        content.textContent = `${nextTaskTitle} — due in ${days} day${days === 1 ? '' : 's'}`;
    } else {
        //one or more hours
        const hours = Math.floor(minutes / 60);
        content.textContent = `${nextTaskTitle} — due in ${hours} hour${hours === 1 ? '' : 's'}`;
    }
}


// ---------- Init ----------
loadTasks();
loadNextTask();
loadCategories();
setInterval(updateCountdown, 60000);



