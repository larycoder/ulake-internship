// Step 1: Import React
import * as React from 'react'
import axios from 'axios'
import Layout from "../components/layout";
import { StaticImage } from 'gatsby-plugin-image'


const loadUsers = async () => {
    const fetchUsers = () => axios.get("http://user.ulake.sontg.net/api/user");
    const users = await fetchUsers();
    if (users.data.code === 200) {
        for (const user of users.data.resp) {
            console.log(user.userName, user.email);
        }
    }
}

// Step 2: Define your component
const IndexPage = () => {
    return (
        <Layout pageTitle="Home Page">
            <p>I'm making this by following the Gatsby Tutorial.</p>
            <StaticImage
                alt="Clifford, a reddish-brown pitbull, posing on a couch and looking stoically at the camera"
                src="../images/icon.png"
                onClick={loadUsers}
            />
        </Layout>
    )
}

// Step 3: Export your component
export default IndexPage