import React from "react"
import Layout from "../components/layout";
import { getUser } from "../services/auth"

function Profile() {
    const user = getUser();
    return (
        <Layout>
            <h1>Your profile</h1>
            <ul>
                <li>Name: {user.name}</li>
                <li>User name : {user.username}</li>
                <li>E-mail: {user.email}</li>
            </ul>
        </Layout>
    )
}
export default Profile