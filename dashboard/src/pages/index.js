// Step 1: Import React
import * as React from 'react'
import axios from 'axios'
import Layout from "../components/layout";
import { StaticImage } from 'gatsby-plugin-image'


const fetchUsers = () => {
    axios.get("http://user.ulake.sontg.net/api/user")
        .then((resp) => {
            console.log(resp);
        })
    console.log("abcdef");
    return "something";
}

// Step 2: Define your component
const IndexPage = () => {
    return (
        <Layout pageTitle="Home Page">
            <p>I'm making this by following the Gatsby Tutorial.</p>
            <StaticImage
                alt="Clifford, a reddish-brown pitbull, posing on a couch and looking stoically at the camera"
                src="../images/icon.png"
                onClick={fetchUsers}
            />
        </Layout>
    )
}

// Step 3: Export your component
export default IndexPage