import { useState, useEffect } from 'react'
import { describeGap } from '../format.js'

function NextTask({ task }) {
    const [now, setNow] = useState(() => Date.now())

    useEffect(() => {
        const id = setInterval(() => setNow(Date.now()), 60_000)
        return () => clearInterval(id)
    }, [])

    if (!task)
        return <div id="next-task">No Upcoming Tasks</div>
    
    return <div id="next-task">{task.title} - {describeGap(task.deadline - now)}</div>
}

export default NextTask