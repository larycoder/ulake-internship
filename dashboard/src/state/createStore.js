
import { createStore as reduxCreateStore } from "redux"

const actions = {
    increment: `INCREMENT`,
    jwtObtained: "jwt/obtained"
}

const reducer = (state, action) => {
    switch (action.type) {
        case actions.increment: {
            return {...state,
                count: state.count + 1,
            }
        }
        case actions.jwtObtained: {
            return {...state, 
                jwt: action.payload
            }
        }
        default:
            return state;
    }
}

const initialState = { 
    count: 0,
    jwt: ""
}

const createStore = () => reduxCreateStore(reducer, initialState)
export default createStore
export { actions }

