import * as React from 'react'
import NavBar from './navbar'

const Layout = ({ pageTitle, children }) => {
    return (<>
        <NavBar pageTitle = {pageTitle}/>
        <main>
            {children}
        </main>
        </>
    )
}
export default Layout