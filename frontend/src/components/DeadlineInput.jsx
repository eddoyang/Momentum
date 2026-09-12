import { useEffect, useRef } from 'react'
import flatpickr from 'flatpickr'

function DeadlineInput({ value, onChange }) {
    const inputRef = useRef(null)
    const fpRef = useRef(null)
    const onChangeRef = useRef(onChange)
    onChangeRef.current = onChange
    
    useEffect(() => {
        fpRef.current = flatpickr(inputRef.current, {
            enableTime: true,
            dateFormat: 'Y-m-d H:i',
            time_24hr: false,
            onChange: ([date]) => onChangeRef.current(date ?? null)
        })
        
        return () => { fpRef.current.destroy(); fpRef.current= null } 
    }, [])
    
    useEffect(() => {
        if (!fpRef.current) return
        if (value) fpRef.current.setDate(value, false)
        else fpRef.current.clear()
    }, [value])

    return <input ref={inputRef} placeholder="deadline" />
}

export default DeadlineInput