
import { useState, useRef, useEffect } from 'react'
import { parseTask } from '../api.js'

function QuickAdd({ onParsed }) {
    const [text, setText] = useState('')
    const [pending, setPending] = useState(false)
    const [failed, setFailed] = useState(false)
    const abortRef = useRef(null)

    useEffect(() => () => abortRef.current?.abort(), [])

    async function handleParse(e) {
        e.preventDefault()
        if (!text.trim()) return

        abortRef.current?.abort()
        const controller = new AbortController()
        abortRef.current = controller

        setPending(true)
        setFailed(false)

        try {
            const draft = await parseTask(text, controller.signal)
            onParsed({ title: draft.title ?? '', category: draft.category ?? '', deadline: draft.deadline ? new Date(draft.deadline) : null})
            setText('')
        } catch (err) {
            if (err.name === 'AbortError') return
            setFailed(true)
            onParsed({ title: text, category: '', deadline: null })
        } finally {
            setPending(false)
        }
    }

    return (
        <form id="nl-add-panel" onSubmit={handleParse} autoComplete="off">
            <input id="nl-input" value={text} disabled={pending} onChange={e => setText(e.target.value)} placeholder="e.g. Math quiz on friday 1pm" required />
            <button type="submit" disabled={pending || !text.trim()}>
                {pending ? 'Parsing...' : 'Parse'}
            </button>
            {failed && <span role="status">Couldn't parse. Filled in text instead.</span>}
        </form>
    )
}

export default QuickAdd