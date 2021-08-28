import React from "react"
import PropTypes from "prop-types"
import { navigate } from "gatsby"
import { isLoggedIn } from "../services/auth"

const PrivateRoute = ({ component: Component, location, ...rest }) => {
    if (!isLoggedIn() && location.pathname !== `/dashboard/signin`) {
        navigate(`/dashboard/signin`);
        return null;
    }

    return <Component {...rest} />
}

PrivateRoute.propTypes = {
    component: PropTypes.any.isRequired,
}

export default PrivateRoute