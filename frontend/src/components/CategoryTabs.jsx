import { DragDropProvider } from '@dnd-kit/react'
import { useSortable } from '@dnd-kit/react/sortable'
import { move } from '@dnd-kit/helpers'

function CategoryTabs({ categories, activeCategory, onSelect, onReorder }) {
    return (
        <DragDropProvider onDragEnd={(e) => onReorder(move(categories, e))}>
            <div id="tabs-display">
                <button className={`category-tab${activeCategory=== 'All' ? ' active' : ''}`} onClick={() => onSelect('All')}>All</button>
                {categories.map((name, index) => (<Tab key={name} id={name} index={index} active={name === activeCategory} onSelect={onSelect} />))}
            </div>
        </DragDropProvider>
    )
}


function Tab({ id, index, active, onSelect }) {
    const { ref } = useSortable({ id, index})
    
    return (
        <button ref={ref} className={`category-tab${active ? ' active' : ''}`} onClick={() => onSelect(id)}>
            {id}
        </button>
    )
}

export default CategoryTabs

