import { Router } from '@reach/router';
import React from 'react';
import Layout from '../components/layout';
import Login from '../components/login';
import PrivateRoute from '../components/privateroute';
import Signin from './dashboard/signin';
import Users from './dashboard/users';
import Profile from './profile';

/**
 * Handle authentication check for private routes 
 * @returns Dashboard component
 */
function Dashboard() {
    return (
        <Router basepath="/dashboard">
            <PrivateRoute path="/users" component={Users} />
            <PrivateRoute path="/profile" component={Profile} />
            <Login path="/login" />
        </Router>
    )
}

export default Dashboard;