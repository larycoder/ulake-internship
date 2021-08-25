import React, { useState } from "react"
import { navigate } from "gatsby"
import { handleLogin, isLoggedIn } from "../services/auth"
import Layout from "./layout"


function handleUpdate(event, state, setState) {
    let newState = Object.assign({}, state);
    newState[event.target.name] = event.target.value;
    setState(newState);
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
                    <input type="text" name="username" onChange={(event) => {handleUpdate(event, state, setState)}} />
                </label>
                <label>
                    Password
                    <input
                        type="password"
                        name="password"
                        onChange={(event) => {handleUpdate(event, state, setState)}}
                    />
                </label>
                <input type="submit" value="Log In" />
            </form>
        </Layout>
    )
}

export default Login