import * as React from 'react'
import { Link, navigate } from 'gatsby'
import {
    container,
    heading,
    navLinks,
    navLinkItem,
    navLinkText
} from './layout.module.css'
import { isLoggedIn, logout } from '../services/auth'


const NavBar = ({ pageTitle }) => {
    return (
        <div className={container}>
            <title>{pageTitle}</title>
            <nav>
                <ul className={navLinks}>
                    <li className={navLinkItem}><Link to="/" className={navLinkText}>Home</Link></li>
                    <li className={navLinkItem}><Link to="/dashboard/users" className={navLinkText}>Users</Link></li>
                    <li className={navLinkItem}><Link to="/about" className={navLinkText}>About</Link></li>
                    <li className={navLinkItem}><Link to="/dashboard/signin" className={navLinkText}>Sign in</Link></li>
                    {
                        (!isLoggedIn() ? (
                            <li className={navLinkItem}><Link to="/dashboard/login" className={navLinkText}>Login</Link></li>
                        ) :
                        (
                            <li className={navLinkItem}><a href="/" className={navLinkText} 
                            onClick={event => {
                                event.preventDefault()
                                logout(() => navigate(`/dashboard/login`))
                              }}>Logout</a></li>
                        ))
                    }
                </ul>
            </nav>
            <h1 className={heading}>{pageTitle}</h1>
        </div>
    )
}
export default NavBar