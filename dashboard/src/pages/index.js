import React from 'react'
import Layout from "../components/layout";
import { isLoggedIn, getUser } from "../services/auth"
import { connect } from 'react-redux'
import PropTypes from "prop-types"
import { obtainJwt } from '../state/jwt';
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

const ConnectedCounter = connect(mapStateToProps, mapDispatchToProps)(Counter)

const Index = () => (
    <Layout pageTitle="Home Page">
        <p>Welcome back.</p>
        <ConnectedCounter />
        <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
    </Layout>
)

export default Index