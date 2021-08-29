import React from 'react'
import { useSelector } from 'react-redux'
import Layout from "../components/layout"
import { isLoggedIn, getUser } from "../services/auth"
import ConnectedCounter from '../components/counter'

const Index = () => {
    const count = useSelector((state) => state.count)
    return (
        <Layout pageTitle="Home Page">
            <p>Welcome back #{count} times.</p>
            <ConnectedCounter />
            <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
        </Layout>
    )
}

export default Index