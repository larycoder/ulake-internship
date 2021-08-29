import React from 'react'
import Layout from "../components/layout";
import { isLoggedIn, getUser } from "../services/auth"
import ConnectedCounter from '../components/counter';

const Index = () => (
    <Layout pageTitle="Home Page">
        <p>Welcome back.</p>
        <ConnectedCounter />
        <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
    </Layout>
)

export default Index