import React, { useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import Layout from "../components/layout"
import { isLoggedIn, getUser } from "../services/auth"
import ConnectedCounter from '../components/counter'
import { actions } from '../state/createStore'


const Index = () => {
    const count = useSelector((state) => state.count);
    const dispatch = useDispatch();
    const increase = useCallback(
        () => dispatch({type: actions.increment}),
        [dispatch]
    );
    return (
        <Layout pageTitle="Home Page">
            <p>Welcome back #{count} times.</p>
            <ConnectedCounter />
            <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
            <button onClick={increase}>++</button>
        </Layout>
    )
}

export default Index