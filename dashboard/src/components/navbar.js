import * as React from 'react'
import { Link } from 'gatsby'
import {
    container,
    heading,
    navLinks,
    navLinkItem,
    navLinkText
} from './layout.module.css'
import { isLoggedIn } from '../services/auth'


const NavBar = ({ pageTitle }) => {
    let loginUrl = isLoggedIn()? "login" : "logout";
    return (
        <div className={container}>
            <title>{pageTitle}</title>
            <nav>
                <ul className={navLinks}>
                    <li className={navLinkItem}><Link to="/" className={navLinkText}>Home</Link></li>
                    <li className={navLinkItem}><Link to="/dashboard/users" className={navLinkText}>Users</Link></li>
                    <li className={navLinkItem}><Link to="/about" className={navLinkText}>About</Link></li>
                    <li className={navLinkItem}><Link to="/dashboard/login" className={navLinkText}>{loginUrl}</Link></li>
                </ul>
            </nav>
            <h1 className={heading}>{pageTitle}</h1>
        </div>
    )
}
export default NavBar