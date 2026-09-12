export function formatDeadline(date) {
    const datePart = date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric'})
    const hh = String(date.getHours()).padStart(2, '0')
    const mm = String(date.getMinutes()).padStart(2, '0')
    return `${datePart} (${hh}:${mm})`
}

export function describeGap(ms) {
    if (ms < 0)
        return `Overdue`
    
    const minutes = Math.floor(ms / 60000)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)

    if (days > 0) 
        return `due in ${days} day${days === 1 ? '' : 's'}`
    if (hours > 0) 
        return `due in ${hours} hour${hours === 1 ? '' : 's'}`
    if (minutes > 0)
        return `due in ${minutes} minute${minutes === 1 ? '' : 's'}`
    
    return `due in less than a minute`
}