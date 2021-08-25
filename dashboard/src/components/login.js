import React, { useState } from "react"
import { navigate } from "gatsby"
import { handleLogin, isLoggedIn } from "../services/auth"
import Layout from "./layout"


function handleUpdate(event, setState) {
    setState({
        [event.target.name]: event.target.value,
    });
}

function handleSubmit(event, state) {
    event.preventDefault();
    handleLogin(state);
}


function Login() {
    if (isLoggedIn()) {
        navigate(`/dashboard/profile`)
    }

    const [state, setState] = useState({
        username: ``,
        password: ``,
    });


    return (
        <Layout>
            <h1>Log in</h1>
            <form
                method="post"
                onSubmit={event => {
                    handleSubmit(event, state);
                    navigate(`/dashboard/profile`);
                }}
            >
                <label>
                    Username
                    <input type="text" name="username" onChange={handleUpdate} />
                </label>
                <label>
                    Password
                    <input
                        type="password"
                        name="password"
                        onChange={handleUpdate}
                    />
                </label>
                <input type="submit" value="Log In" />
            </form>
        </Layout>
    )
}

export default Login