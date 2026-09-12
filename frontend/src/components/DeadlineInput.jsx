function DeadlineInput({ value, onChange }) {
    const local = value ? new Date(value.getTime() - value.getTimezoneOffset() * 60000).toISOString().slice(0, 16) : ''
    
    return (
        <input type="datetime-local" value={local} onChange={e => onChange(e.target.value ? new Date(e.target.value): null)} />
    )
}


export default DeadlineInput