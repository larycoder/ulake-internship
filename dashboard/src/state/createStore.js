
import { createStore as reduxCreateStore } from "redux"

const actions = {
    increment: `INCREMENT`
}

const reducer = (state, action) => {
    if (action.type === actions.increment) {
        return Object.assign({}, state, {
            count: state.count + 1,
        })
    }
    return state
}

const initialState = { count: 0 }

const createStore = () => reduxCreateStore(reducer, initialState)
export default createStore
export { actions }

