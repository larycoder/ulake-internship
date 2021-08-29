import * as React from 'react'
import { useSelector } from 'react-redux'
import Layout from "../components/layout"

const About = () => {
    const count = useSelector((state) => state.count)
    return (
        <Layout pageTitle="About Me">
            <p>Hi there #{count} times!</p>
        </Layout>
    )
}

export default About