import React from 'react'
import { connect } from 'react-redux'
import PropTypes from "prop-types"
import { actions } from '../state/createStore';

const Counter = ({ count, increment }) => (
    <div>
        <p>Count: {count}</p>
        <button onClick={increment}>Increment</button>
    </div>
)

Counter.propTypes = {
    count: PropTypes.number.isRequired,
    increment: PropTypes.func.isRequired,
}

const mapStateToProps = ({ count }) => {
    return { count }
}

const mapDispatchToProps = dispatch => {
    return { increment: () => dispatch({ type: actions.increment }) }
}

const ConnectedCounter = connect(mapStateToProps, mapDispatchToProps)(Counter);
export default ConnectedCounter;