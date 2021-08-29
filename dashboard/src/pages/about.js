import * as React from 'react'
import Layout from "../components/layout";
import ConnectedCounter from '../components/counter';

const AboutPage = () => {
    return (
        <Layout pageTitle="About Me">
            <p>Hi there! I'm the proud creator of this site, which I built with Gatsby.</p>
            <ConnectedCounter />
        </Layout>
    )
}
// Step 3: Export your component
export default AboutPage