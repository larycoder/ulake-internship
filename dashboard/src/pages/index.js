import React from 'react'
import Layout from "../components/layout";
import { isLoggedIn, getUser } from "../services/auth"

function Index() {
    return (
        <Layout pageTitle="Home Page">
            <p>Welcome back.</p>            
            <h1>Hello {isLoggedIn() ? getUser().name : "world"}!</h1>
        </Layout>
    );
}

export default Index