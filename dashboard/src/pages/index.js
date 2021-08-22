import * as React from 'react'
import axios from 'axios'
import Layout from "../components/layout";
import { StaticImage } from 'gatsby-plugin-image'
import { Link } from 'gatsby';
import { TimelineSharp } from '@material-ui/icons';


class IndexQuery extends React.Component {
    state = {
        users: []
    }
    loadUsers = async () => {;
        const users = await axios.get("http://user.ulake.sontg.net/api/user");
        if (users.data.code === 200) {
            let extractUsers = users.data.resp.map(user => {return {name: user.userName, email: user.email}});
            this.setState({users: extractUsers});
        }
    }

    componentDidMount() {
        this.loadUsers();
    }

    render() {
        const {users} = this.state;
        return (
            <Layout pageTitle="Home Page">
                <p>List of available users</p>
                <ul>
                {users.map(user => (
                    <li key={user.name}>
                        <Link to={`./user/${user.name}`}>
                            {user.name}, {user.email}
                        </Link>
                    </li>
                ))}
                </ul>
            </Layout>
        )
    }

// Step 3: Export your component
}

export default IndexQuery