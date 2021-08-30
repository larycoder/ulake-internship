import { GetAPI } from "./api"

export const isBrowser = () => typeof window !== "undefined"

export const getUser = () =>
    isBrowser() && window.localStorage.getItem("gatsbyUser")
        ? JSON.parse(window.localStorage.getItem("gatsbyUser"))
        : {}

const setUser = user =>
    window.localStorage.setItem("gatsbyUser", JSON.stringify(user))

export const handleLogin = async ({ username, password }) => {
    console.log("prepare to login");
    const resp = await GetAPI("user", "/api/user");
    console.log(resp);
    if (username === `john` && password === `pass`) {
        return setUser({
            username: `john`,
            name: `Johnny`,
            email: `johnny@example.org`,
        })
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