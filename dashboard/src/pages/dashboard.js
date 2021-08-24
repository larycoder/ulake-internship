import { Router } from '@reach/router';
import React from 'react';
import Layout from '../components/layout';
import PrivateRoute from '../components/privateroute';
import Signin from './dashboard/signin';
import Users from './dashboard/users';
import Profile from './profile';

function Dashboard() {
    return (
        <Layout>
            <Router basepath="/dashboard">
                <PrivateRoute path="/users" component={Users} />
                <PrivateRoute path="/profile" component={Profile} />
                <Signin path="/login" />
            </Router>
        </Layout>
    )
}

export default Dashboard;