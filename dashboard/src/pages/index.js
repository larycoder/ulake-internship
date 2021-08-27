import React from 'react'
import Layout from "../components/layout";
import { isLoggedIn, getUser } from "../services/auth"
import { connect } from 'react-redux'
import { obtainJwt } from '../state/jwt';

const Index = ({token, dispatch}) => (
    <Layout pageTitle="Home Page">
        <p>Welcome back.</p>            
        <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
        <button onClick = {() => {dispatch(obtainJwt("12345"))}}>{token}</button>

    </Layout>
)

export default connect(state => ({
    token: state.jwt.token
}), null)(Index)