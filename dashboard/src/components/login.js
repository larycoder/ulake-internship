import React from "react"
import { navigate } from "gatsby"
import { handleLogin, isLoggedIn } from "../services/auth"
import Layout from "./layout"

class Login extends React.Component {
    state = {
        username: ``,
        password: ``,
    }
    handleUpdate = event => {
        this.setState({
            [event.target.name]: event.target.value,
        })
    }
    handleSubmit = event => {
        event.preventDefault()
        handleLogin(this.state)
    }
    render() {
        if (isLoggedIn()) {
            navigate(`/dashboard/profile`)
        }
        return (
            <Layout>
                <h1>Log in</h1>
                    <form
                        method="post"
                        onSubmit={event => {
                            this.handleSubmit(event)
                            navigate(`/dashboard/profile`)
                        }}
                    >
                    <label>
                        Username
                        <input type="text" name="username" onChange={this.handleUpdate} />
                    </label>
                    <label>
                        Password
                        <input
                            type="password"
                            name="password"
                            onChange={this.handleUpdate}
                        />
                    </label>
                    <input type="submit" value="Log In" />
                </form>
            </Layout>
        )
    }
}
export default Login