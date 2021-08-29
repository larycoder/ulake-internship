import axios from "axios"
import { backend } from "./api"

export const isBrowser = () => typeof window !== "undefined"

export const getUser = () => {
    // TODO: use redux
    return isBrowser() && window.localStorage.getItem("gatsbyUser")
        ? JSON.parse(window.localStorage.getItem("gatsbyUser"))
        : {}
}

const setUser = user => {
    // TODO: use redux
    window.localStorage.setItem("gatsbyUser", JSON.stringify(user))
}

export const login = async ({ username, password }) => {
    const resp = await axios.get(backend.endpoint.user + "/api/auth/login");
    if (resp.data.code === 200) {            
        return setUser({
            username: `john`,
            name: `Johnny`,
            email: `johnny@example.org`,
        });
    }
    return false;
}

export const isLoggedIn = () => {
    const user = getUser()

    return !!user.username
}

export const logout = callback => {
    setUser({})
    callback()
}